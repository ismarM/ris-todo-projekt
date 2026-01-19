import { useEffect, useState } from "react";
import api from "./api";
import "./index.css";

function App() {
    const [tasks, setTasks] = useState([]);
    const [newTask, setNewTask] = useState("");
    const [newDesc, setNewDesc] = useState("");
    const [newDifficulty, setNewDifficulty] = useState("Medium");
    // const [syncedTasks, setSyncedTasks] = useState([]);

    const [newEmail, setNewEmail] = useState("");
    const [newReminderEnabled, setNewReminderEnabled] = useState(false);
    const [editEmail, setEditEmail] = useState("");
    const [editReminderEnabled, setEditReminderEnabled] = useState(false);

    const [newRecurrenceType, setNewRecurrenceType] = useState("NONE");



    // const [newDueDate, setNewDueDate] = useState("");
    // ob zagonu aplikacije input type="date" že imel današnji datum
    const [newDueDate, setNewDueDate] = useState(() => {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, "0");
        const dd = String(today.getDate()).padStart(2, "0");
        return `${yyyy}-${mm}-${dd}`;
    });

    const [analytics, setAnalytics] = useState(null);

    // helper za lep prikaz datuma
    function formatDate(d) {
        if (!d) return "";
        const [y, m, day] = String(d).split("-").map(Number); // pričakujemo ISO: yyyy-mm-dd
        if (!y || !m || !day) return d;
        return `${String(day).padStart(2, "0")}. ${String(m).padStart(2, "0")}. ${y}`;
    }

    function daysLeft(dueDate) {
        if (!dueDate) return null;

        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const due = new Date(dueDate);
        due.setHours(0, 0, 0, 0);

        const diffMs = due - today;
        const diffDays = Math.round(diffMs / (1000 * 60 * 60 * 24));

        let label;
        let cssClass;

        if (diffDays < 0) {
            label = "Overdue!";
            cssClass = "deadline-badge overdue";
        } else if (diffDays === 0) {
            label = "Today!";
            cssClass = "deadline-badge today";
        } else if (diffDays === 1) {
            label = "Tomorrow!";
            cssClass = "deadline-badge tomorrow";
        } else if (diffDays <= 3) {
            label = `${diffDays} days left`;
            cssClass = "deadline-badge soon";
        } else {
            label = `${diffDays} days left`;
            cssClass = "deadline-badge normal";
        }

        return { diffDays, label, cssClass }
    }


    const [filterDate, setFilterDate] = useState("");

    const [editId, setEditId] = useState(null);
    const [editTitle, setEditTitle] = useState("");
    const [editDesc, setEditDesc] = useState("");
    const [editDueDate, setEditDueDate] = useState("");
    const [editDifficulty, setEditDifficulty] = useState("Medium");
    const [editRecurrenceType, setEditRecurrenceType] = useState("NONE");


// PREBERI VSE (ob zagonu)
    useEffect(() => {
        loadTasks();
        loadAnalytics();
    }, [filterDate]);                                                             // filtriranje

    async function loadTasks() {
        const url = filterDate ? `/tasks?dueDate=${filterDate}` : "/tasks";     // filtriranje: bb vsaki spremembi datuma React pošlje poizvedbo na backend
        const res = await api.get(url);
        setTasks(res.data);
    }

// DODAJ
    async function addTask() {
        const t = newTask.trim();
        if (!t) return;

        await api.post("/tasks", {
            title: t,
            description: newDesc || "",
            dueDate: newDueDate || null,  // <-- uporabi state (ISO yyyy-mm-dd je OK za LocalDate)
            difficulty: newDifficulty,
            email: newEmail || null,
            reminderEnabled: newReminderEnabled,
            recurrenceType: newRecurrenceType,
            done: false,
        });

        // reseti
        setNewTask("");
        setNewDesc("");
        setNewDifficulty("Medium");
        setNewEmail("");
        setNewReminderEnabled(false);
        setNewRecurrenceType("NONE");

        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, "0");
        const dd = String(today.getDate()).padStart(2, "0");
        setNewDueDate(`${yyyy}-${mm}-${dd}`);

        // loadTasks();
        await loadTasks();
        await loadAnalytics();
    }

// OZNAČI/ODOZNAČI
    async function toggleDone(task) {
        await api.put(`/tasks/${task.id}`, {
            title: task.title,
            description: task.description ?? "",
            dueDate: task.dueDate ?? null,
            difficulty: task.difficulty ?? "Medium",
            email: task.email ?? null,
            reminderEnabled: task.reminderEnabled ?? false,
            recurrenceType: task.recurrenceType ?? "NONE",
            done: !task.done,
        });
        // loadTasks();
        await loadTasks();
        await loadAnalytics();
    }

// BRIŠI
    async function deleteTask(id) {
        await api.delete(`/tasks/${id}`);
        // loadTasks();
        await loadTasks();
        await loadAnalytics();
    }

    async function syncToCalendar(taskId) {
        try {
          const res = await api.post(`/tasks/${taskId}/calendar-sync`);
      
          // po syncu reloadamo task-e, da dobimo calendarSyncStatus iz backenda
          await loadTasks();
      
          // res.data je CalendarEventDTO (objekt), zato ga lepo prikažemo
          alert(
            `Sinhronizacija uspešna!`
          );
        } catch (err) {
          await loadTasks(); // da se status ERROR pokaže, če ga backend nastavi
          alert("Napaka pri sinhronizaciji (preveri, če ima task nastavljen dueDate).");
        }
      }

// UREJANJE
    // začni urejati
    function startEdit(task) {
        setEditId(task.id);
        setEditTitle(task.title ?? "");
        setEditDesc(task.description ?? "");
        setEditDueDate(task.dueDate ?? "");
        setEditDifficulty(task.difficulty || "Medium")
        setEditEmail(task.email || "");
        setEditReminderEnabled(task.reminderEnabled ?? false);
        setEditRecurrenceType(task.recurrenceType ?? "NONE");
        console.log("task.difficulty = ", task.difficulty);
    }

    // prekliči urejanje
    function cancelEdit() {
        setEditId(null);
        setEditTitle("");
        setEditDesc("");
        setEditDueDate("");
        setEditDifficulty("Medium");
        setEditEmail("");
        setEditReminderEnabled(false);
        setEditRecurrenceType("NONE");
    }

    // shrani (PUT) – posodobimo samo title, ostalo nespremenjeno
    async function saveEdit(task) {
        const title = editTitle.trim();
        if (!title) return;

        await api.put(`/tasks/${task.id}`, {
            title,
            description: editDesc,
            dueDate: editDueDate || null,
            email: editEmail || null,
            reminderEnabled: editReminderEnabled,
            difficulty: editDifficulty,
            recurrenceType: editRecurrenceType,
            done: task.done,
        });

        cancelEdit();
        // loadTasks();
        await loadTasks();
        await loadAnalytics();
    }

// izračun tasksView
    const tasksView = [...tasks]
        .sort((a, b) => {
            if (!a.dueDate && !b.dueDate) return 0;
            if (!a.dueDate) return 1;
            if (!b.dueDate) return -1;
            return new Date(a.dueDate) - new Date(b.dueDate);
        });

    //API klic za analitiko
    async function loadAnalytics() {
    try {
        const res = await api.get("/tasks/analytics");
        setAnalytics(res.data);
        console.log("Analytics:", res.data);
    } catch (err) {
        console.error("Napaka pri pridobivanju analitike:", err);
    }
}


    // JSX
    return (
        <div className="page">
            <div className="card">
            {/* HEADER */}
            <div className="header">
                <div>
                    <h1 className="title">To-Do</h1>
                    <p className="subtitle">
                        Uredi opravila, ponovitve in roke na enem mestu.
                    </p>
                </div>
            </div>

            {/* LAYOUT: LEVO + DESNO */}
            <div className="layout">
                {/* LEVO – NOVO OPRAVILO */}
                <div className="panel">
                    <div className="panel-title">Novo opravilo</div>

                    <div className="add-row">
                        <input
                        type="text"
                        placeholder="Vpiši opravilo..."
                        value={newTask}
                        onChange={(e) => setNewTask(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && addTask()}
                        />

                        <textarea
                        placeholder="Opis opravila..."
                        value={newDesc}
                        onChange={(e) => setNewDesc(e.target.value)}
                        />

                        <input
                        type="email"
                        placeholder="E-pošta za opomnik (neobvezno)"
                        value={newEmail}
                        onChange={(e) => setNewEmail(e.target.value)}
                        />

                        <label className="reminder-row">
                        <input
                            type="checkbox"
                            checked={newReminderEnabled}
                            onChange={(e) => setNewReminderEnabled(e.target.checked)}
                        />
                        <span>Pošlji opomnik dan pred rokom</span>
                        </label>

                        <input
                        type="date"
                        value={newDueDate}
                        onChange={(e) => setNewDueDate(e.target.value)}
                        />

                        <select
                        value={newDifficulty}
                        onChange={(e) => setNewDifficulty(e.target.value)}
                        >
                        <option value="Low">Low</option>
                        <option value="Medium">Medium</option>
                        <option value="High">High</option>
                        </select>

                        <select
                        value={newRecurrenceType}
                        onChange={(e) => setNewRecurrenceType(e.target.value)}
                        >
                        <option value="NONE">Brez ponavljanja</option>
                        <option value="DAILY">Dnevno</option>
                        <option value="WEEKLY">Tedensko</option>
                        <option value="MONTHLY">Mesečno</option>
                        </select>

                        <button
                        className="btn primary"
                        onClick={addTask}
                        disabled={!newTask.trim()}
                        >
                        Add
                        </button>

                        
                    </div>
                    {/* FILTRIRANJE */}
                    <div className="filter-row">
                        <label>Filtriraj po datumu:&nbsp;</label>
                        <input
                            type="date"
                            value={filterDate}
                            onChange={(e) => setFilterDate(e.target.value)}
                        />
                        {filterDate && (
                            <button
                            className="btn ghost sm"
                            onClick={() => setFilterDate("")}
                            >
                            Show all
                            </button>
                        )}
                    </div>
                </div>

                {/* DESNO – PREGLED / ANALITIKA */}
                <div className="panel">
                <div className="panel-title">Pregled</div>

                {analytics && (
                    <div className="analytics-card">
                    <h2>Analitika produktivnosti</h2>

                    <div className="analytics-grid">
                        <div className="analytics-item">
                        <span className="label">Skupno opravil</span>
                        <span className="value">{analytics.skupnoStevilo}</span>
                        </div>

                        <div className="analytics-item">
                        <span className="label">Dokončana</span>
                        <span className="value done">
                            {analytics.steviloDokoncanih}
                        </span>
                        </div>

                        <div className="analytics-item">
                        <span className="label">Nedokončana</span>
                        <span className="value">
                            {analytics.steviloNedokoncanih}
                        </span>
                        </div>

                        <div className="analytics-item">
                        <span className="label">Zapadla</span>
                        <span className="value overdue">
                            {analytics.steviloZapadlih}
                        </span>
                        </div>

                        <div className="analytics-item wide">
                        <span className="label">Odstotek dokončanih</span>
                        <span className="value percent">
                            {analytics.odstotekDokoncanih.toFixed(1)} %
                        </span>
                        </div>
                    </div>
                    </div>
                )}
                </div>
            </div>

                

                <ul className="list">
                    {tasksView.length === 0 && <li className="empty">No tasks.</li>}

                    {tasksView.map((task) => {
                        // če trenutno urejamo ta task -----------------------------------------------------------------
                        if (editId === task.id) {
                            return (
                                <li key={task.id} className="item edit-mode">
                                    <div className="left">
                                        <div className="task-edit-fields">
                                            <input
                                                type="text"
                                                value={editTitle}
                                                onChange={(e) => setEditTitle(e.target.value)}
                                                placeholder="Naslov opravila..."
                                            />
                                            <textarea
                                                value={editDesc}
                                                onChange={(e) => setEditDesc(e.target.value)}
                                                placeholder="Opis opravila..."
                                            />
                                            <input
                                                type="date"
                                                value={editDueDate}
                                                onChange={(e) => setEditDueDate(e.target.value)}
                                            />
                                            <select
                                                value={editDifficulty}
                                                onChange={(e) => setEditDifficulty(e.target.value)}
                                            >
                                                <option value="Low">Low</option>
                                                <option value="Medium">Medium</option>
                                                <option value="High">High</option>
                                            </select>
                                            <select
                                                value={editRecurrenceType}
                                                onChange={(e) => setEditRecurrenceType(e.target.value)}
                                            >
                                                <option value="NONE">Brez ponavljanja</option>
                                                <option value="DAILY">Dnevno</option>
                                                <option value="WEEKLY">Tedensko</option>
                                                <option value="MONTHLY">Mesečno</option>
                                            </select>

                                            <input
                                                type="email"
                                                value={editEmail}
                                                onChange={(e) => setEditEmail(e.target.value)}
                                                placeholder="E-pošta za opomnik (neobvezno)"
                                            />
                                            <label className="reminder-row">
                                                <input
                                                    type="checkbox"
                                                    checked={editReminderEnabled}
                                                    onChange={(e) =>
                                                        setEditReminderEnabled(e.target.checked)
                                                    }
                                                />
                                                <span>Pošlji opomnik dan pred rokom</span>
                                            </label>
                                        </div>
                                    </div>

                                    <div className="action-buttons">
                                        <button className="btn primary" onClick={() => saveEdit(task)}>Save</button>
                                        <button className="btn ghost" onClick={cancelEdit}>Cancel</button>
                                    </div>
                                </li>
                            );
                        }

                        // če task ni v načinu urejanja ----------------------------------------------------------------
                        return (
                            <li
                                key={task.id}
                                className={`item ${
                                    task.recurrenceType === "DAILY"
                                    ? "daily"
                                    : task.recurrenceType === "WEEKLY"
                                    ? "weekly"
                                    : task.recurrenceType === "MONTHLY"
                                    ? "monthly"
                                    : ""
                                }`}
                            >
                                <div className="left">
                                    <input
                                        type="checkbox"
                                        checked={task.done}
                                        onChange={() => toggleDone(task)}
                                    />

                                    <div className="task-info">
                                    <span className={`text ${task.done ? "done" : ""}`}>{task.title}</span>
                                        <div className="details">
                                            {/* PRVA VRSTICA: difficulty + days left */}
                                            <div className="details-top-row">
                                                {(() => {
                                                const difficulty = task.difficulty || "Medium";
                                                const diffClass =
                                                    difficulty === "Low"
                                                    ? "difficulty-low"
                                                    : difficulty === "High"
                                                    ? "difficulty-high"
                                                    : "difficulty-medium";

                                                return (
                                                    <span className={`difficulty-badge ${diffClass}`}>
                                                    {difficulty}
                                                    </span>
                                                );
                                                })()}

                                                {task.recurrenceType && task.recurrenceType !== "NONE" && (
                                                    <span
                                                        className={`recurrence-badge ${
                                                        task.recurrenceType === "DAILY"
                                                            ? "recurrence-daily"
                                                            : task.recurrenceType === "WEEKLY"
                                                            ? "recurrence-weekly"
                                                            : "recurrence-monthly"
                                                        }`}
                                                    >
                                                        🔁 {task.recurrenceType === "DAILY"
                                                        ? "Dnevno"
                                                        : task.recurrenceType === "WEEKLY"
                                                        ? "Tedensko"
                                                        : "Mesečno"}
                                                    </span>
                                                )}

                                                {task.dueDate && !task.done && (() => {
                                                const info = daysLeft(task.dueDate);
                                                if (!info) return null;

                                                return (
                                                    <span className={info.cssClass}>
                                                    {info.label}
                                                    </span>
                                                );
                                                })()}
                                            </div>

                                            {/* DRUGA VRSTICA: datum + opomnik*/}
                                            {task.dueDate && (
                                                <div className="details-date">
                                                    <small>🗓 {formatDate(task.dueDate)}</small>

                                                    {task.reminderEnabled && task.email && (
                                                        <span className="reminder-badge">
                                                        🔔 Opomnik na: {task.email}
                                                    </span>
                                                    )}
                                                </div>
                                            )}

                                            {/* TRETJA VRSTICA: opis */}
                                            {task.description && (
                                                <div className="details-desc">
                                                    <small>{task.description}</small>
                                                </div>
                                            )}
                                            {task.calendarSyncStatus && (
                                                <div className="calendar-status">
                                                    {task.calendarSyncStatus === "IN_PROGRESS" && (
                                                        <span className="calendar-badge in-progress">🟡 V TEKU</span>
                                                    )}
                                                    {task.calendarSyncStatus === "SUCCESS" && (
                                                        <span className="calendar-badge success">✅ USPEŠNO</span>
                                                    )}
                                                    {task.calendarSyncStatus === "ERROR" && (
                                                        <span className="calendar-badge error">❌ NAPAKA</span>
                                                    )}
                                                </div>
                                            )}
                                            </div>

                                    </div>
                                </div>

                                <div className="action-buttons">
                                    <button className="btn ghost" onClick={() => startEdit(task)}>Edit</button>
                                    <button className="btn danger" onClick={() => deleteTask(task.id)}>Delete</button>
                                    <button className="btn ghost" onClick={() => syncToCalendar(task.id)}>Sync to calendar</button>
                                </div>
                            </li>
                        );
                    })}
                </ul>
            </div>
        </div>
    );
}

export default App;