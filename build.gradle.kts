// Arquivo de build do projeto raiz: só declara os plugins usados pelo módulo :app.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
}
