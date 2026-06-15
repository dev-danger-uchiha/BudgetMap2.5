import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class RestoreUI {
    public static void main(String[] args) throws IOException {
        String base = "src/main/resources/static/";
        
        // 1. mi-establecimiento.html
        Path p1 = Paths.get(base + "aliado/mi-establecimiento.html");
        String c1 = new String(Files.readAllBytes(p1), StandardCharsets.UTF_8);
        c1 = c1.replace("<button type=\"submit\" id=\"btn-guardar\"", 
                        "<div class=\"flex items-center justify-between bg-slate-50 p-4 rounded-xl border border-slate-200\">\n" +
                        "    <div>\n" +
                        "        <h4 class=\"font-bold text-slate-800 text-sm\">Aceptar Reservas a través de BudgetMap</h4>\n" +
                        "        <p class=\"text-xs text-slate-500\">Permite que los usuarios reserven boletos o aparten aforo desde la aplicación.</p>\n" +
                        "    </div>\n" +
                        "    <label class=\"relative inline-flex items-center cursor-pointer\">\n" +
                        "        <input type=\"checkbox\" id=\"est-reservas\" class=\"sr-only peer\" checked>\n" +
                        "        <div class=\"w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-emerald-500\"></div>\n" +
                        "    </label>\n" +
                        "</div>\n<button type=\"submit\" id=\"btn-guardar\"");
        c1 = c1.replace("document.getElementById('est-rut').value = data.rut || '';",
                        "document.getElementById('est-rut').value = data.rut || '';\n                    document.getElementById('est-reservas').checked = data.reservasHabilitadas !== false;");
        c1 = c1.replace("rut: document.getElementById('est-rut').value,",
                        "rut: document.getElementById('est-rut').value,\n                    reservasHabilitadas: document.getElementById('est-reservas').checked,");
        Files.write(p1, c1.getBytes(StandardCharsets.UTF_8));

        // 2. detalle.html
        Path p2 = Paths.get(base + "explorador/detalle.html");
        String c2 = new String(Files.readAllBytes(p2), StandardCharsets.UTF_8);
        c2 = c2.replace("if(tipo === 'evento') {\n                    document.getElementById('det-precio').innerText = data.precio > 0 ? `$${data.precio}` : 'Gratis';\n                }",
                        "if(tipo === 'evento') {\n                    document.getElementById('det-precio').innerText = data.precio > 0 ? `$${data.precio}` : 'Gratis';\n                    if(data.requiereReserva === false) {\n                        document.getElementById('btn-reservar').parentElement.parentElement.style.display = 'none';\n                    }\n                } else if(tipo === 'establecimiento') {\n                    if(data.reservasHabilitadas === false) {\n                        document.getElementById('btn-reservar').parentElement.parentElement.style.display = 'none';\n                    }\n                }");
        Files.write(p2, c2.getBytes(StandardCharsets.UTF_8));

        // 3. mis-reservas.html
        Path p3 = Paths.get(base + "explorador/mis-reservas.html");
        String c3 = new String(Files.readAllBytes(p3), StandardCharsets.UTF_8);
        c3 = c3.replace("<p class=\"text-lg font-black text-orange-600\">${r.numeroPersonas} Pax</p>\n                        </div>",
                        "<p class=\"text-lg font-black text-orange-600\">${r.numeroPersonas} Pax</p>\n                        </div>\n                        <div class=\"bg-slate-50 px-4 py-2 rounded-xl border border-slate-100\">\n                            <p class=\"text-[9px] font-black text-slate-400 uppercase\">${r.estado === 'COMPLETADA' ? 'Puntos Ganados' : 'Puntos Potenciales'}</p>\n                            <p class=\"text-lg font-black text-emerald-600\">+${r.puntosOtorgados || 0}</p>\n                        </div>");
        Files.write(p3, c3.getBytes(StandardCharsets.UTF_8));

        // 4. mis-eventos.html
        Path p4 = Paths.get(base + "anfitrion/mis-eventos.html");
        String c4 = new String(Files.readAllBytes(p4), StandardCharsets.UTF_8);
        c4 = c4.replace("</button>` : `<span class=\"text-[9px] text-slate-400 font-bold tracking-widest uppercase\">CERRADO</span>`}",
                        "</button>` : `<span class=\"text-[9px] text-slate-400 font-bold tracking-widest uppercase\">CERRADO</span>`}");
        c4 = c4.replace("${estadoVirtual === 'ACTIVA' ? `<button onclick=\"eliminarEvento(${ev.id})\" class=\"text-[10px] font-bold text-red-500 hover:text-red-700 bg-red-50 px-2 py-1 rounded uppercase tracking-widest\">Cancelar</button>`",
                        "${estadoVirtual === 'ACTIVA' ? `<button onclick=\"verReservas(${ev.id})\" class=\"text-[10px] font-bold text-blue-500 hover:text-blue-700 bg-blue-50 px-2 py-1 rounded uppercase tracking-widest mr-1\">Reservas</button><button onclick=\"eliminarEvento(${ev.id})\" class=\"text-[10px] font-bold text-red-500 hover:text-red-700 bg-red-50 px-2 py-1 rounded uppercase tracking-widest\">Cancelar</button>`");
        
        c4 = c4.replace("async function eliminarEvento(id) {",
                        "async function verReservas(eventoId) {\n            document.getElementById('modal-reservas').classList.remove('hidden');\n            const lista = document.getElementById('lista-reservas-modal');\n            lista.innerHTML = '<p class=\"text-sm text-center\">Cargando reservas...</p>';\n            try {\n                const res = await fetch(`https://budgetmap-api.onrender.com/api/mis-reservas/evento/${eventoId}`, { headers: { 'Authorization': `Bearer ${token}` } });\n                if (res.ok) {\n                    const reservas = await res.json();\n                    if(reservas.length === 0) {\n                        lista.innerHTML = '<p class=\"text-xs text-center text-slate-500\">No hay reservas aún.</p>';\n                    } else {\n                        lista.innerHTML = reservas.map(r => `\n                            <div class=\"border-b py-2 flex justify-between items-center\">\n                                <div>\n                                    <p class=\"text-xs font-bold\">${r.codigoReserva} <span class=\"bg-slate-200 text-slate-600 px-1 rounded\">${r.estado}</span></p>\n                                    <p class=\"text-[10px] text-slate-500\">${r.numeroPersonas} personas - ${new Date(r.fechaReserva).toLocaleDateString()}</p>\n                                </div>\n                            </div>\n                        `).join('');\n                    }\n                }\n            } catch(e) { lista.innerHTML = '<p class=\"text-red-500 text-sm\">Error de conexión</p>'; }\n        }\n        function cerrarModalReservas() { document.getElementById('modal-reservas').classList.add('hidden'); }\n\n        async function eliminarEvento(id) {");
        
        c4 = c4.replace("</body>",
                        "    <!-- Modal Reservas -->\n    <div id=\"modal-reservas\" class=\"fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-50 hidden flex justify-center items-center p-4\">\n        <div class=\"bg-white w-full max-w-md rounded-2xl p-6 shadow-2xl\">\n            <div class=\"flex justify-between items-center mb-4\">\n                <h3 class=\"text-lg font-black text-slate-800 uppercase tracking-widest\">Reservas del Evento</h3>\n                <button onclick=\"cerrarModalReservas()\" class=\"text-slate-400 hover:text-red-500 font-bold\">&#10005;</button>\n            </div>\n            <div id=\"lista-reservas-modal\" class=\"max-h-60 overflow-y-auto space-y-2\"></div>\n        </div>\n    </div>\n</body>");
        Files.write(p4, c4.getBytes(StandardCharsets.UTF_8));
        
        System.out.println("UI Restored");
    }
}
