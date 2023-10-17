package me.srrapero720.watermedia;

import java.util.prefs.Preferences;

public class VerificarProgramaInstalado {
    public static void main(String[] args) {
        String programa = "TLauncher"; // Nombre del programa que quieres verificar

        Preferences prefs = Preferences.userRoot().node("Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall");
        String[] programasInstalados;
        try {
            programasInstalados = prefs.childrenNames();
            for (String programaInstalado : programasInstalados) {
                String nombre = prefs.node(programaInstalado).get("DisplayName", "");
                if (nombre.contains(programa)) {
                    System.out.println(programa + " está instalado en el sistema.");
                    return;
                }
            }
            System.out.println(programa + " no está instalado en el sistema.");
        } catch (Exception e) {
            System.out.println("Error al acceder al registro: " + e.getMessage());
        }
    }
}