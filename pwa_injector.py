import os
import glob

static_dir = r"c:\PROYECTO REAL\budgetmap-api\src\main\resources\static"
html_files = glob.glob(os.path.join(static_dir, "**/*.html"), recursive=True)

head_injection = """  <!-- PWA Setup -->
  <link rel="manifest" href="/manifest.json">
  <meta name="theme-color" content="#f97316">
  <link rel="apple-touch-icon" href="/images/logo.png">
"""

body_injection = """
<!-- PWA Service Worker -->
<script>
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker.register('/sw.js')
        .then(reg => console.log('Service Worker registrado', reg))
        .catch(err => console.error('Error al registrar Service Worker', err));
    });
  }
</script>
</body>
"""

for filepath in html_files:
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        if '<link rel="manifest"' in content:
            continue  # ya inyectado

        # Inject in <head>
        content = content.replace("</head>", head_injection + "</head>")
        
        # Inject before </body>
        content = content.replace("</body>", body_injection)

        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
            
        print(f"Updated {filepath}")
    except Exception as e:
        print(f"Error processing {filepath}: {e}")
