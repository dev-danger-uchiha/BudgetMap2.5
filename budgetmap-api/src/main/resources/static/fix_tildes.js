const fs = require('fs');
const path = require('path');

const dir = "c:/PROYECTO REAL/budgetmap-api/src/main/resources/static";

function walk(dir) {
    let results = [];
    const list = fs.readdirSync(dir);
    list.forEach(function(file) {
        file = dir + '/' + file;
        const stat = fs.statSync(file);
        if (stat && stat.isDirectory()) { 
            results = results.concat(walk(file));
        } else { 
            if (file.endsWith('.html')) results.push(file);
        }
    });
    return results;
}

const htmlFiles = walk(dir);

const replacements = {
    "Descripcin": "Descripción",
    "Trminos": "Términos",
    "Telfono": "Teléfono",
    "Mximo": "Máximo",
    "Ubicacin": "Ubicación",
    "Direccin": "Dirección",
    "Gestin": "Gestión",
    "Informacin": "Información",
    "Configuracin": "Configuración",
    "Contrasea": "Contraseña",
    "Validacin": "Validación",
    "Prximos": "Próximos",
    "conexin": "conexión",
    "crtico": "crítico",
    "vlida": "válida",
    "CDIGO": "CÓDIGO",
    "Cdigo": "Código",
    "PsBLICO": "PÚBLICO",
    "Aadir": "Añadir",
    "Reserva": "¡Reserva",
    "Informacin": "¡Información",
    "Y\"?": "📍",
    "Y\".": "📅",
    "Y??": "🏁",
    "YZY?": "🎫",
    "Categora": "Categoría",
    "categora": "categoría",
    "da": "día",
    "Da": "Día",
    "Ao": "Año",
    "ao": "año",
    "Accin": "Acción",
    "accin": "acción",
    "seccin": "sección",
    "Seccin": "Sección",
    "Ttulo": "Título",
    "ttulo": "título",
    "Mǭs": "Más",
    "mǭs": "más",
    "Aqu": "Aquí",
    "aqu": "aquí",
    "Aadir": "Añadir",
    "aadir": "añadir",
    "Inici": "Inició",
    "inici": "inició",
    "TambiǸn": "También",
    "tambiǸn": "también",
    "Estǭ": "Está",
    "estǭ": "está",
    "S": "Sí",
    "s": "sí",
    "Opciones de Gestin": "Opciones de Gestión",
    "Previsualizacin": "Previsualización",
    "PsBLICO": "PÚBLICO",
    "C\"DIGO": "CÓDIGO",
    "Descripcin": "Descripción",
    "Trminos": "Términos",
    "Telfono": "Teléfono",
    "Mximo": "Máximo",
    "Ubicacin": "Ubicación",
    "Direccin": "Dirección",
    "Gestin": "Gestión",
    "Informacin": "Información",
    "Configuracin": "Configuración",
    "Contrasea": "Contraseña",
    "Validacin": "Validación",
    "Prximos": "Próximos",
    "conexin": "conexión",
    "crtico": "crítico",
    "vlida": "válida",
    "Cdigo": "Código",
    "Reserva": "¡Reserva",
    "Informacin": "¡Información",
    "Y\"?": "📍",
    "Y\".": "📅",
    "Y??": "🏁",
    "YZ": "🎫",
    "YZY": "🎫"
};

for (const f of htmlFiles) {
    let content = fs.readFileSync(f, 'utf8');
    
    // Quick replacements based on the object
    let changed = false;
    for (const [bad, good] of Object.entries(replacements)) {
        if (content.includes(bad)) {
            content = content.split(bad).join(good);
            changed = true;
        }
    }
    
    // Also use the actual unicode replacement character
    const corruptChar = '\ufffd';
    const genericFixes = {
        [`Descripci${corruptChar}n`]: "Descripción",
        [`Descripci${corruptChar}`]: "Descripción",
        [`T${corruptChar}rminos`]: "Términos",
        [`Tel${corruptChar}fono`]: "Teléfono",
        [`M${corruptChar}ximo`]: "Máximo",
        [`Ubicaci${corruptChar}n`]: "Ubicación",
        [`Direcci${corruptChar}n`]: "Dirección",
        [`Gesti${corruptChar}n`]: "Gestión",
        [`Informaci${corruptChar}n`]: "Información",
        [`Configuraci${corruptChar}n`]: "Configuración",
        [`Contrase${corruptChar}a`]: "Contraseña",
        [`Validaci${corruptChar}n`]: "Validación",
        [`Pr${corruptChar}ximos`]: "Próximos",
        [`conexi${corruptChar}n`]: "conexión",
        [`cr${corruptChar}tico`]: "crítico",
        [`v${corruptChar}lida`]: "válida",
        [`C${corruptChar}DIGO`]: "CÓDIGO",
        [`C${corruptChar}digo`]: "Código",
        [`P${corruptChar}sBLICO`]: "PÚBLICO",
        [`A${corruptChar}adir`]: "Añadir",
        [`${corruptChar}Reserva`]: "¡Reserva",
        [`${corruptChar}Informaci${corruptChar}n`]: "¡Información",
        [`C${corruptChar}"DIGO`]: "CÓDIGO",
        [`${corruptChar}Y"?`]: "📍",
        [`${corruptChar}Y".`]: "📅",
        [`${corruptChar}Y??`]: "🏁",
        [`${corruptChar}YZY?`]: "🎫",
        [`Categor${corruptChar}a`]: "Categoría",
        [`categor${corruptChar}a`]: "categoría",
        [`d${corruptChar}a`]: "día",
        [`D${corruptChar}a`]: "Día",
        [`A${corruptChar}o`]: "Año",
        [`a${corruptChar}o`]: "año",
        [`Acci${corruptChar}n`]: "Acción",
        [`acci${corruptChar}n`]: "acción",
        [`secci${corruptChar}n`]: "sección",
        [`Secci${corruptChar}n`]: "Sección",
        [`T${corruptChar}tulo`]: "Título",
        [`t${corruptChar}tulo`]: "título",
        [`M${corruptChar}s`]: "Más",
        [`m${corruptChar}s`]: "más",
        [`Aqu${corruptChar}`]: "Aquí",
        [`aqu${corruptChar}`]: "aquí",
        [`A${corruptChar}adir`]: "Añadir",
        [`a${corruptChar}adir`]: "añadir",
        [`Inici${corruptChar}`]: "Inició",
        [`inici${corruptChar}`]: "inició",
        [`Tambi${corruptChar}n`]: "También",
        [`tambi${corruptChar}n`]: "también",
        [`Est${corruptChar}`]: "Está",
        [`est${corruptChar}`]: "está",
        [`S${corruptChar}`]: "Sí",
        [`s${corruptChar}`]: "sí",
        [`Opciones de Gesti${corruptChar}n`]: "Opciones de Gestión",
        [`Previsualizaci${corruptChar}n`]: "Previsualización"
    };

    for (const [bad, good] of Object.entries(genericFixes)) {
        if (content.includes(bad)) {
            content = content.split(bad).join(good);
            changed = true;
        }
    }
    
    if (changed) {
        fs.writeFileSync(f, content, 'utf8');
        console.log("Fixed:", f);
    }
}
console.log("Done");
