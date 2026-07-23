plugins { alias(libs.plugins.memoria.android.library) }
android { namespace = "chirag127.memoria.data.repository" }
dependencies {
    implementation(project(":domain"))
    implementation(project(":data:vault"))
    implementation(project(":data:git"))
    implementation(project(":data:ai"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
}
