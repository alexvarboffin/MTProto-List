#!/usr/bin/env python3
"""Viewer for MTProto-List Firebase JSON exports."""

from __future__ import annotations

import copy
import json
import sys
import threading
import tkinter as tk
from pathlib import Path
from tkinter import filedialog, messagebox, ttk
from typing import Any, Callable

from firebase_resolver import resolve_geo_for_rows, resolve_type_for_rows

DEFAULT_JSON = Path(__file__).with_name("firebase.json")
META_FIELDS = frozenset({"id", "source", "_geo_error", "_type_error"})

MTPROTO_COLUMNS = (
    ("id", "ID", 220),
    ("host", "Host", 180),
    ("port", "Port", 60),
    ("country", "Country", 120),
    ("code", "Code", 50),
    ("city", "City", 120),
    ("enabled", "Enabled", 70),
    ("source", "Source", 70),
)

WEBPROXY_COLUMNS = (
    ("id", "ID", 220),
    ("proxyUrl", "Proxy URL", 260),
    ("type", "Type", 90),
    ("ip", "IP", 120),
    ("country", "Country", 120),
    ("code", "Code", 50),
    ("enabled", "Enabled", 70),
)


def is_record(value: Any) -> bool:
    return isinstance(value, dict) and bool(value)


def classify_record(record: dict[str, Any]) -> str:
    if record.get("proxyUrl"):
        return "webproxy"
    if record.get("host") and record.get("port"):
        return "mtproto"
    return "unknown"


def parse_firebase(data: dict[str, Any]) -> tuple[list[dict[str, Any]], list[dict[str, Any]], list[dict[str, Any]]]:
    mtproto: list[dict[str, Any]] = []
    webproxy: list[dict[str, Any]] = []
    unknown: list[dict[str, Any]] = []

    def add_record(record_id: str, record: dict[str, Any], source: str) -> None:
        row = {"id": record_id, **record, "source": source}
        kind = classify_record(record)
        if kind == "mtproto":
            mtproto.append(row)
        elif kind == "webproxy":
            webproxy.append(row)
        else:
            unknown.append(row)

    for key, value in data.items():
        if not is_record(value):
            continue
        if key == "a":
            for record_id, record in value.items():
                if is_record(record):
                    add_record(record_id, record, "a")
            continue
        if key == "b":
            for record_id, record in value.items():
                if is_record(record):
                    add_record(record_id, record, "b")
            continue
        add_record(key, value, "legacy")

    return mtproto, webproxy, unknown


def row_to_record(row: dict[str, Any]) -> dict[str, Any]:
    return {key: value for key, value in row.items() if key not in META_FIELDS}


def apply_rows_to_data(data: dict[str, Any], rows: list[dict[str, Any]]) -> None:
    for row in rows:
        record_id = str(row.get("id", ""))
        if not record_id:
            continue
        record = row_to_record(row)
        source = row.get("source", "legacy")
        if source == "a":
            node = data.setdefault("a", {})
            if isinstance(node, dict):
                node[record_id] = record
        elif source == "b":
            node = data.setdefault("b", {})
            if isinstance(node, dict):
                node[record_id] = record
        else:
            data[record_id] = record


def toggle_enabled(rows: list[dict[str, Any]], enabled: bool | None = None) -> int:
    count = 0
    for row in rows:
        if enabled is None:
            row["enabled"] = not bool(row.get("enabled"))
        else:
            row["enabled"] = enabled
        count += 1
    return count


class DatasetTab(ttk.Frame):
    def __init__(
        self,
        master: tk.Misc,
        title: str,
        columns: tuple[tuple[str, str, int], ...],
        kind: str,
        on_resolve_geo: Callable[["DatasetTab", bool], None],
        on_resolve_type: Callable[["DatasetTab", bool], None] | None = None,
        on_toggle_enabled: Callable[["DatasetTab", bool | None], None] | None = None,
    ) -> None:
        super().__init__(master)
        self.title = title
        self.kind = kind
        self.columns = columns
        self.on_resolve_geo = on_resolve_geo
        self.on_resolve_type = on_resolve_type
        self.on_toggle_enabled = on_toggle_enabled
        self.all_rows: list[dict[str, Any]] = []
        self.filtered_rows: list[dict[str, Any]] = []

        toolbar = ttk.Frame(self)
        toolbar.pack(fill=tk.X, padx=8, pady=(8, 4))

        ttk.Label(toolbar, text="Filter:").pack(side=tk.LEFT)
        self.filter_var = tk.StringVar()
        self.filter_var.trace_add("write", lambda *_: self.apply_filter())
        filter_entry = ttk.Entry(toolbar, textvariable=self.filter_var, width=40)
        filter_entry.pack(side=tk.LEFT, padx=(4, 12))

        self.only_enabled_var = tk.BooleanVar(value=False)
        ttk.Checkbutton(
            toolbar,
            text="Only enabled",
            variable=self.only_enabled_var,
            command=self.apply_filter,
        ).pack(side=tk.LEFT)

        self.stats_var = tk.StringVar(value=f"{title}: 0")
        ttk.Label(toolbar, textvariable=self.stats_var).pack(side=tk.RIGHT)

        actions = ttk.Frame(self)
        actions.pack(fill=tk.X, padx=8, pady=(0, 4))
        ttk.Button(actions, text="Geo: selected", command=lambda: self.on_resolve_geo(self, True)).pack(side=tk.LEFT)
        ttk.Button(actions, text="Geo: all", command=lambda: self.on_resolve_geo(self, False)).pack(side=tk.LEFT, padx=(6, 0))
        if on_resolve_type is not None:
            ttk.Button(actions, text="Type: selected", command=lambda: on_resolve_type(self, True)).pack(
                side=tk.LEFT, padx=(12, 0)
            )
            ttk.Button(actions, text="Type: all", command=lambda: on_resolve_type(self, False)).pack(side=tk.LEFT, padx=(6, 0))
        if on_toggle_enabled is not None:
            ttk.Button(actions, text="Toggle enabled", command=lambda: on_toggle_enabled(self, None)).pack(
                side=tk.LEFT, padx=(12, 0)
            )
            ttk.Button(actions, text="Enable", command=lambda: on_toggle_enabled(self, True)).pack(side=tk.LEFT, padx=(6, 0))
            ttk.Button(actions, text="Disable", command=lambda: on_toggle_enabled(self, False)).pack(side=tk.LEFT, padx=(6, 0))

        paned = ttk.Panedwindow(self, orient=tk.VERTICAL)
        paned.pack(fill=tk.BOTH, expand=True, padx=8, pady=(0, 8))

        table_frame = ttk.Frame(paned)
        detail_frame = ttk.LabelFrame(paned, text="Details")
        paned.add(table_frame, weight=3)
        paned.add(detail_frame, weight=1)

        column_ids = [column[0] for column in columns]
        self.tree = ttk.Treeview(
            table_frame,
            columns=column_ids,
            show="headings",
            selectmode="browse",
        )
        for column_id, heading, width in columns:
            self.tree.heading(column_id, text=heading, command=lambda c=column_id: self.sort_by(c))
            self.tree.column(column_id, width=width, anchor=tk.W)

        y_scroll = ttk.Scrollbar(table_frame, orient=tk.VERTICAL, command=self.tree.yview)
        x_scroll = ttk.Scrollbar(table_frame, orient=tk.HORIZONTAL, command=self.tree.xview)
        self.tree.configure(yscrollcommand=y_scroll.set, xscrollcommand=x_scroll.set)

        self.tree.grid(row=0, column=0, sticky="nsew")
        y_scroll.grid(row=0, column=1, sticky="ns")
        x_scroll.grid(row=1, column=0, sticky="ew")
        table_frame.rowconfigure(0, weight=1)
        table_frame.columnconfigure(0, weight=1)

        self.detail_text = tk.Text(detail_frame, wrap=tk.NONE, height=8, font=("Consolas", 10))
        detail_scroll = ttk.Scrollbar(detail_frame, orient=tk.VERTICAL, command=self.detail_text.yview)
        self.detail_text.configure(yscrollcommand=detail_scroll.set)
        self.detail_text.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=6, pady=6)
        detail_scroll.pack(side=tk.RIGHT, fill=tk.Y, pady=6)

        self.tree.bind("<<TreeviewSelect>>", self.on_select)
        self.tree.bind("<Double-1>", self.on_double_click)
        self.sort_reverse: dict[str, bool] = {}

    def set_rows(self, rows: list[dict[str, Any]]) -> None:
        self.all_rows = rows
        self.apply_filter()

    def apply_filter(self) -> None:
        query = self.filter_var.get().strip().lower()
        only_enabled = self.only_enabled_var.get()
        rows: list[dict[str, Any]] = []
        for row in self.all_rows:
            if only_enabled and not row.get("enabled"):
                continue
            if query:
                haystack = " ".join(str(value) for value in row.values()).lower()
                if query not in haystack:
                    continue
            rows.append(row)
        self.filtered_rows = rows
        self.refresh_table()

    def refresh_table(self) -> None:
        self.tree.delete(*self.tree.get_children())
        column_ids = [column[0] for column in self.columns]
        for index, row in enumerate(self.filtered_rows):
            values = [self.format_cell(row.get(column_id, "")) for column_id in column_ids]
            self.tree.insert("", tk.END, iid=str(index), values=values)
        enabled_count = sum(1 for row in self.filtered_rows if row.get("enabled"))
        self.stats_var.set(f"Shown: {len(self.filtered_rows)} / {len(self.all_rows)} | enabled: {enabled_count}")
        self.detail_text.delete("1.0", tk.END)

    def sort_by(self, column_id: str) -> None:
        reverse = self.sort_reverse.get(column_id, False)
        self.filtered_rows.sort(key=lambda row: str(row.get(column_id, "")).lower(), reverse=reverse)
        self.sort_reverse[column_id] = not reverse
        self.refresh_table()

    def on_select(self, _event: tk.Event) -> None:
        selection = self.tree.selection()
        if not selection:
            return
        row = self.filtered_rows[int(selection[0])]
        self.show_row_details(row)

    def on_double_click(self, event: tk.Event) -> None:
        if self.on_toggle_enabled is None:
            return
        row_id = self.tree.identify_row(event.y)
        if not row_id:
            return
        self.tree.selection_set(row_id)
        self.on_toggle_enabled(self, None)

    def show_row_details(self, row: dict[str, Any]) -> None:
        self.detail_text.delete("1.0", tk.END)
        self.detail_text.insert(tk.END, json.dumps(row, indent=2, ensure_ascii=False))

    @staticmethod
    def format_cell(value: Any) -> str:
        if isinstance(value, bool):
            return "yes" if value else "no"
        return "" if value is None else str(value)

    def get_selected_rows(self) -> list[dict[str, Any]]:
        selection = self.tree.selection()
        if not selection:
            return []
        return [self.filtered_rows[int(item_id)] for item_id in selection]

    def get_target_rows(self, selected_only: bool) -> list[dict[str, Any]]:
        if selected_only:
            return self.get_selected_rows()
        return list(self.all_rows)

    def update_row_in_table(self, row: dict[str, Any]) -> None:
        row_id = row.get("id")
        for index, filtered_row in enumerate(self.filtered_rows):
            if filtered_row.get("id") == row_id:
                column_ids = [column[0] for column in self.columns]
                values = [self.format_cell(filtered_row.get(column_id, "")) for column_id in column_ids]
                self.tree.item(str(index), values=values)
                break
        selection = self.tree.selection()
        if selection:
            selected = self.filtered_rows[int(selection[0])]
            if selected.get("id") == row_id:
                self.show_row_details(selected)


class FirebaseViewerApp(tk.Tk):
    def __init__(self) -> None:
        super().__init__()
        self.title("MTProto-List Firebase Viewer")
        self.geometry("1200x760")
        self.minsize(900, 600)

        self.file_path = tk.StringVar(value=str(DEFAULT_JSON) if DEFAULT_JSON.exists() else "")
        self._busy = False
        self._dirty = False
        self.root_data: dict[str, Any] | None = None

        top = ttk.Frame(self)
        top.pack(fill=tk.X, padx=8, pady=8)

        ttk.Button(top, text="Open JSON...", command=self.open_file).pack(side=tk.LEFT)
        ttk.Button(top, text="Reload", command=self.reload_current).pack(side=tk.LEFT, padx=(6, 0))
        ttk.Button(top, text="Save JSON", command=self.save_file).pack(side=tk.LEFT, padx=(6, 0))
        ttk.Button(top, text="Save JSON as...", command=self.save_file_as).pack(side=tk.LEFT, padx=(6, 0))
        ttk.Label(top, textvariable=self.file_path).pack(side=tk.LEFT, padx=(12, 0))

        self.notebook = ttk.Notebook(self)
        self.notebook.pack(fill=tk.BOTH, expand=True, padx=8, pady=(0, 8))

        self.mtproto_tab = DatasetTab(
            self.notebook,
            "MTProto",
            MTPROTO_COLUMNS,
            kind="mtproto",
            on_resolve_geo=self.start_geo_resolve,
            on_toggle_enabled=self.toggle_enabled_selected,
        )
        self.webproxy_tab = DatasetTab(
            self.notebook,
            "WebProxy",
            WEBPROXY_COLUMNS,
            kind="webproxy",
            on_resolve_geo=self.start_geo_resolve,
            on_resolve_type=self.start_type_resolve,
            on_toggle_enabled=self.toggle_enabled_selected,
        )
        self.unknown_tab = DatasetTab(
            self.notebook,
            "Unknown",
            MTPROTO_COLUMNS,
            kind="unknown",
            on_resolve_geo=self.start_geo_resolve,
            on_toggle_enabled=self.toggle_enabled_selected,
        )

        self.notebook.add(self.mtproto_tab, text="MTProto")
        self.notebook.add(self.webproxy_tab, text="WebProxy")
        self.notebook.add(self.unknown_tab, text="Unknown")

        self.status_var = tk.StringVar(value="Open a firebase.json file to begin.")
        ttk.Label(self, textvariable=self.status_var, anchor=tk.W).pack(fill=tk.X, padx=8, pady=(0, 8))

        if DEFAULT_JSON.exists():
            self.load_file(DEFAULT_JSON)

    def open_file(self) -> None:
        path = filedialog.askopenfilename(
            title="Open Firebase JSON",
            initialdir=str(Path(__file__).parent),
            filetypes=[("JSON files", "*.json"), ("All files", "*.*")],
        )
        if path:
            self.load_file(Path(path))

    def reload_current(self) -> None:
        current = self.file_path.get().strip()
        if not current:
            messagebox.showinfo("Reload", "No file selected.")
            return
        if self._dirty and not messagebox.askyesno("Reload", "Discard unsaved changes?"):
            return
        self.load_file(Path(current))

    def load_file(self, path: Path) -> None:
        try:
            with path.open("r", encoding="utf-8-sig") as handle:
                data = json.load(handle)
        except OSError as error:
            messagebox.showerror("Open failed", str(error))
            return
        except json.JSONDecodeError as error:
            messagebox.showerror("Invalid JSON", str(error))
            return

        if not isinstance(data, dict):
            messagebox.showerror("Invalid format", "Root JSON value must be an object.")
            return

        self.root_data = data
        self._dirty = False
        mtproto, webproxy, unknown = parse_firebase(data)
        self.mtproto_tab.set_rows(mtproto)
        self.webproxy_tab.set_rows(webproxy)
        self.unknown_tab.set_rows(unknown)

        self.file_path.set(str(path))
        self.status_var.set(
            f"Loaded {path.name}: MTProto={len(mtproto)}, WebProxy={len(webproxy)}, Unknown={len(unknown)}"
        )

    def toggle_enabled_selected(self, tab: DatasetTab, enabled: bool | None) -> None:
        rows = tab.get_selected_rows()
        if not rows:
            messagebox.showinfo("Enabled", "Select a row first.")
            return
        changed = toggle_enabled(rows, enabled)
        self._dirty = True
        tab.apply_filter()
        tab.show_row_details(rows[0])
        state = "enabled" if rows[0].get("enabled") else "disabled"
        action = "toggled" if enabled is None else ("enabled" if enabled else "disabled")
        self.status_var.set(f"{tab.title}: {action} {changed} item(s); last row is {state} (unsaved)")

    def collect_export_data(self) -> dict[str, Any]:
        if self.root_data is None:
            raise ValueError("No data loaded")
        data = copy.deepcopy(self.root_data)
        apply_rows_to_data(data, self.mtproto_tab.all_rows)
        apply_rows_to_data(data, self.webproxy_tab.all_rows)
        apply_rows_to_data(data, self.unknown_tab.all_rows)
        return data

    def save_file(self) -> None:
        path_text = self.file_path.get().strip()
        if not path_text:
            self.save_file_as()
            return
        self._write_json(Path(path_text))

    def save_file_as(self) -> None:
        path = filedialog.asksaveasfilename(
            title="Save Firebase JSON",
            initialdir=str(Path(__file__).parent),
            initialfile="firebase.json",
            defaultextension=".json",
            filetypes=[("JSON files", "*.json"), ("All files", "*.*")],
        )
        if path:
            self._write_json(Path(path))
            self.file_path.set(path)

    def _write_json(self, path: Path) -> None:
        try:
            data = self.collect_export_data()
            with path.open("w", encoding="utf-8") as handle:
                json.dump(data, handle, indent=2, ensure_ascii=False)
                handle.write("\n")
        except (OSError, ValueError) as error:
            messagebox.showerror("Save failed", str(error))
            return
        self._dirty = False
        self.status_var.set(f"Saved {path.name}")

    def start_geo_resolve(self, tab: DatasetTab, selected_only: bool) -> None:
        rows = tab.get_target_rows(selected_only)
        if not rows:
            messagebox.showinfo("Geo resolve", "No rows selected." if selected_only else "No rows in tab.")
            return
        if tab.kind == "unknown":
            messagebox.showinfo("Geo resolve", "Unknown records cannot be resolved automatically.")
            return
        self._run_background(
            label=f"Geo resolve ({tab.title})",
            worker=lambda: resolve_geo_for_rows(rows, tab.kind),
            on_done=lambda result, t=tab: self._after_geo_resolve(t, result),
        )

    def start_type_resolve(self, tab: DatasetTab, selected_only: bool) -> None:
        if tab.kind != "webproxy":
            return
        rows = tab.get_target_rows(selected_only)
        if not rows:
            messagebox.showinfo("Type resolve", "No rows selected." if selected_only else "No rows in tab.")
            return
        self._run_background(
            label=f"Type resolve ({tab.title})",
            worker=lambda: resolve_type_for_rows(rows),
            on_done=lambda result, t=tab: self._after_type_resolve(t, result),
        )

    def _run_background(self, label: str, worker: Callable[[], tuple[int, int]], on_done: Callable[[tuple[int, int]], None]) -> None:
        if self._busy:
            messagebox.showinfo("Busy", "Another resolve task is already running.")
            return
        self._busy = True
        self.status_var.set(f"{label}: running...")

        def task() -> None:
            try:
                result = worker()
            except Exception as error:  # noqa: BLE001 - show any worker failure in UI
                self.after(0, lambda: self._on_worker_failed(label, error))
            else:
                self.after(0, lambda: on_done(result))

        threading.Thread(target=task, daemon=True).start()

    def _on_worker_failed(self, label: str, error: Exception) -> None:
        self._busy = False
        self.status_var.set(f"{label}: failed")
        messagebox.showerror(label, str(error))

    def _after_geo_resolve(self, tab: DatasetTab, result: tuple[int, int]) -> None:
        success, errors = result
        self._dirty = True
        tab.apply_filter()
        self._busy = False
        self.status_var.set(f"Geo resolve ({tab.title}): ok={success}, errors={errors}")
        if errors:
            messagebox.showwarning("Geo resolve", f"Completed with errors: {errors}")

    def _after_type_resolve(self, tab: DatasetTab, result: tuple[int, int]) -> None:
        success, errors = result
        self._dirty = True
        tab.apply_filter()
        self._busy = False
        self.status_var.set(f"Type resolve ({tab.title}): ok={success}, errors={errors}")
        if errors:
            messagebox.showwarning("Type resolve", f"Completed with errors: {errors}")

    def destroy(self) -> None:
        super().destroy()


def main() -> int:
    app = FirebaseViewerApp()
    app.mainloop()
    return 0


if __name__ == "__main__":
    sys.exit(main())
