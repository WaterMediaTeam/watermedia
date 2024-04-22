package me.srrapero720.watermedia;

import java.util.Arrays;
import java.util.prefs.Preferences;

public class VerificarProgramaInstalado {
    public static void main(String[] args) {
        String programa = "Discord"; // Nombre del programa que quieres verificar

        Preferences prefs = Preferences.userRoot().node("Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall");
        String[] programasInstalados;
        try {
            programasInstalados = prefs.childrenNames();
            System.out.println("userrot keys are: " + Arrays.toString(Preferences.userRoot().keys()));
            for (String programaInstalado : programasInstalados) {
                System.out.println(Arrays.toString(prefs.node(programaInstalado).keys()));

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