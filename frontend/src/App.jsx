import { useEffect, useState } from "react";
import api from "./api";
import "./index.css";

function App() {
    const [tasks, setTasks] = useState([]);
    const [newTask, setNewTask] = useState("");
    const [newDesc, setNewDesc] = useState("");

    // const [newDueDate, setNewDueDate] = useState("");
    // ob zagonu aplikacije input type="date" že imel današnji datum
    const [newDueDate, setNewDueDate] = useState(() => {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, "0");
        const dd = String(today.getDate()).padStart(2, "0");
        return `${yyyy}-${mm}-${dd}`;
    });



    // helper za lep prikaz datuma
    function formatDate(d) {
        if (!d) return "";
        const [y, m, day] = String(d).split("-").map(Number); // pričakujemo ISO: yyyy-mm-dd
        if (!y || !m || !day) return d;
        return `${String(day).padStart(2, "0")}. ${String(m).padStart(2, "0")}. ${y}`;
    }

    const [filterDate, setFilterDate] = useState("");

    const [editId, setEditId] = useState(null);
    const [editTitle, setEditTitle] = useState("");
    const [editDesc, setEditDesc] = useState("");
    const [editDueDate, setEditDueDate] = useState("");


// PREBERI VSE (ob zagonu)
    useEffect(() => {
        loadTasks();
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
            done: false,
        });

        // reseti
        setNewTask("");
        setNewDesc("");
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, "0");
        const dd = String(today.getDate()).padStart(2, "0");
        setNewDueDate(`${yyyy}-${mm}-${dd}`);

        loadTasks();
    }

// OZNAČI/ODOZNAČI
    async function toggleDone(task) {
        await api.put(`/tasks/${task.id}`, {
            title: task.title,
            description: task.description ?? "",
            dueDate: task.dueDate ?? null,
            done: !task.done,
        });
        loadTasks();
    }

// BRIŠI
    async function deleteTask(id) {
        await api.delete(`/tasks/${id}`);
        loadTasks();
    }

// UREJANJE
    // začni urejati
    function startEdit(task) {
        setEditId(task.id);
        setEditTitle(task.title ?? "");
        setEditDesc(task.description ?? "");
        setEditDueDate(task.dueDate ?? "");
    }

    // prekliči urejanje
    function cancelEdit() {
        setEditId(null);
        setEditTitle("");
        setEditDesc("");
        setEditDueDate("");
    }

    // shrani (PUT) – posodobimo samo title, ostalo nespremenjeno
    async function saveEdit(task) {
        const title = editTitle.trim();
        if (!title) return;

        await api.put(`/tasks/${task.id}`, {
            title,
            description: editDesc,
            dueDate: editDueDate || null,
            done: task.done,
        });

        cancelEdit();
        loadTasks();
    }

// izračun tasksView
    const tasksView = [...tasks]
        .sort((a, b) => {
            if (!a.dueDate && !b.dueDate) return 0;
            if (!a.dueDate) return 1;
            if (!b.dueDate) return -1;
            return new Date(a.dueDate) - new Date(b.dueDate);
        });


    // JSX
    return (
        <div className="page">
            <div className="card">
                <h1>To-Do:</h1>

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
                        type="date"
                        value={newDueDate}
                        onChange={(e) => setNewDueDate(e.target.value)}
                    />
                    <button className="btn primary" onClick={addTask} disabled={!newTask.trim()}>Add</button>


                    {/* FILTRIRANJE */}
                    <div className="filter-row">
                        <label>Filtriraj po datumu:&nbsp;</label>
                        <input
                            type="date"
                            value={filterDate}
                            onChange={(e) => setFilterDate(e.target.value)}
                        />
                        {filterDate && (
                            <button className="btn ghost sm" onClick={() => setFilterDate("")}>
                                Show all
                            </button>
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
                            <li key={task.id} className="item">
                                <div className="left">
                                    <input
                                        type="checkbox"
                                        checked={task.done}
                                        onChange={() => toggleDone(task)}
                                    />

                                    <div className="task-info">
                                        <span className={`text ${task.done ? "done" : ""}`}>{task.title}</span>
                                        <div className="details">
                                            {task.dueDate && <small>🗓 {formatDate(task.dueDate)}</small>}
                                            <br/>
                                            {task.dueDate && task.description ? <small> · </small> : null}
                                            {task.description && <small>{task.description}</small>}
                                        </div>
                                    </div>
                                </div>

                                <div className="action-buttons">
                                    <button className="btn ghost" onClick={() => startEdit(task)}>Edit</button>
                                    <button className="btn ghost" onClick={() => deleteTask(task.id)}>Delete</button>
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