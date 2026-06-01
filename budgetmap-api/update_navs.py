import os
import re

static_dir = r"C:\PROYECTO REAL\budgetmap-api\src\main\resources\static"
exclude_files = ["index.html", "login.html", "register.html"]

logo_svg = """<svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"></path></svg>"""

logout_btn = """<button onclick="cerrarSesion()" class="bg-red-50 text-red-500 border border-red-100 hover:bg-red-500 hover:text-white px-4 py-1.5 rounded-lg text-[10px] font-black uppercase tracking-widest transition-all shadow-sm ml-4">SALIR</button>"""

cerrar_sesion_script = """
    function cerrarSesion() {
        localStorage.clear();
        sessionStorage.clear();
        window.location.href = '/login.html';
    }
"""

def update_file(filepath):
    filename = os.path.basename(filepath)
    if filename in exclude_files:
        return
        
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    if '<nav' not in content:
        return
        
    original_content = content

    # 1. Add cerrarsesion script if missing
    if 'function cerrarSesion' not in content:
        content = content.replace('</script>', cerrar_sesion_script + '\n  </script>', 1)

    # 2. Add BUDGETMAP Logo to the left part of nav
    # We look for the first flex items-center gap-X inside max-w-X flex justify-between
    # A generic regex to match the left-side branding wrapper:
    # <div class="flex items-center gap-...">
    # We'll inject the logo BEFORE the first span or h1 inside it.
    
    # Actually, it's safer to just find:
    # <div class="flex items-center gap-
    # and insert the logo div right inside it.
    if 'BUDGETMAP' not in content and 'd="M9 20l-5.447' not in content:
        # Find the nav block
        nav_match = re.search(r'<nav.*?>.*?</nav>', content, flags=re.DOTALL | re.IGNORECASE)
        if nav_match:
            nav_html = nav_match.group(0)
            
            # The logo wrapper based on role. We will use a generic dark styling if background is light, or white if background is dark.
            # But wait, we can just use bg-slate-800 for the logo wrapper universally, it looks good everywhere.
            logo_div = f'<div class="bg-slate-800 rounded-xl p-2 shadow-inner">{logo_svg}</div><span class="text-xl font-black tracking-widest drop-shadow-md mx-2">BUDGETMAP</span>'
            
            # Find the first `<div class="flex items-center gap-X">` inside nav
            match = re.search(r'<div\s+class="[^"]*flex\s+items-center\s+gap-\d+[^"]*">', nav_html)
            if match:
                insert_pos = match.end()
                new_nav = nav_html[:insert_pos] + '\n        ' + logo_div + nav_html[insert_pos:]
                content = content.replace(nav_html, new_nav)

    # 3. Add Logout button if missing
    if 'SALIR' not in content and 'Cerrar Sesi' not in content and 'cerrarSesion' not in content:
        # Find the right-side flex container. Usually it's the second flex container directly under the max-w-7xl
        # Or simply, we can just inject it right before the closing </div> of the `max-w-7xl flex justify-between` container.
        nav_match = re.search(r'<nav.*?>.*?</nav>', content, flags=re.DOTALL | re.IGNORECASE)
        if nav_match:
            nav_html = nav_match.group(0)
            # Find the end of the flex justify-between container which holds both left and right parts.
            # It's usually the direct child of <nav>
            # <nav...>
            #   <div class="max-w-7xl mx-auto flex justify-between items-center">
            #      ...
            #   </div>
            # </nav>
            # We can find the last </div> before </nav>
            last_div_close = nav_html.rfind('</div>')
            if last_div_close != -1:
                new_nav = nav_html[:last_div_close] + f'\n        {logout_btn}\n      ' + nav_html[last_div_close:]
                content = content.replace(nav_html, new_nav)
    
    # If the file already has a SALIR button but we want to make sure it's uniformly styled or just exists...
    # The user said "poniendo boton de sali en todos". If it's already there, we might not touch it.
    
    if content != original_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {filename}")

if __name__ == "__main__":
    for root, dirs, files in os.walk(static_dir):
        for file in files:
            if file.endswith('.html'):
                update_file(os.path.join(root, file))
