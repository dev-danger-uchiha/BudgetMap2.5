import os
import glob
import re

admin_dir = r"C:\PROYECTO REAL\budgetmap-api\src\main\resources\static\admin"
html_files = glob.glob(os.path.join(admin_dir, "*.html"))

for file_path in html_files:
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    # Primero, convertimos el div de enlaces de hidden md:flex a un scroll horizontal
    content = re.sub(
        r'<div class="flex gap-6 text-xs font-bold uppercase tracking-widest hidden (md|lg):flex">',
        r'<div class="flex gap-4 md:gap-6 text-[10px] md:text-xs font-bold uppercase tracking-widest overflow-x-auto whitespace-nowrap w-full md:w-auto pb-2 md:pb-0 no-scrollbar mt-3 md:mt-0" style="-ms-overflow-style:none; scrollbar-width:none;">\n<style> .no-scrollbar::-webkit-scrollbar { display: none; } </style>',
        content
    )

    # Segundo, cambiamos el contenedor principal de flex a flex-col en mobile
    content = content.replace(
        '<div class="w-full px-6 md:px-10 flex justify-between items-center">',
        '<div class="w-full px-4 md:px-10 flex flex-col md:flex-row justify-between items-center gap-2 md:gap-0">'
    )

    # Tercero, el div izquierdo (logo + titulos) ahora ocupara w-full en mobile y tendra justify-between para poner SALIR a la derecha
    content = content.replace(
        '<div class="flex items-center gap-4">',
        '<div class="flex items-center gap-2 md:gap-4 w-full md:w-auto justify-between md:justify-start">\n                <div class="flex items-center gap-2 md:gap-4">'
    )

    # Cuarto, cerramos ese div agrupado y agregamos el boton SALIR solo para movil
    # Buscaremos donde termina el div izquierdo. Termina justo antes de `<div class="flex gap-4 md:gap-6`
    # Como agregue un div extra, necesito cerrarlo.
    content = content.replace(
        '<div class="flex gap-4 md:gap-6 text-[10px] md:text-xs font-bold uppercase tracking-widest overflow-x-auto',
        '</div>\n                <button onclick="cerrarSesion()" class="text-[10px] font-bold text-red-500 hover:text-red-700 md:hidden bg-red-50 px-2 py-1 rounded">SALIR</button>\n            </div>\n            <div class="flex gap-4 md:gap-6 text-[10px] md:text-xs font-bold uppercase tracking-widest overflow-x-auto'
    )

    # Ocultar CONTROL CENTER en mobile
    content = content.replace(
        '<h1 class="font-black tracking-widest text-emerald-900">CONTROL CENTER</h1>',
        '<h1 class="font-black tracking-widest text-emerald-900 hidden sm:block">CONTROL CENTER</h1>'
    )
    
    # Ocultar el boton SALIR original (que esta a la derecha en desktop) en mobile
    content = re.sub(
        r'<button onclick="cerrarSesion\(\)"\s*class="text-\[12px\] text-slate-400 font-bold hover:text-red-500 transition">SALIR</button>',
        r'<button onclick="cerrarSesion()" class="text-[12px] text-slate-400 font-bold hover:text-red-500 transition hidden md:block">SALIR</button>',
        content
    )

    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)

print("Navbars updated for responsiveness!")
