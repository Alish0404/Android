package com.example.myapp1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "AppPreferences" // Имя SharedPreferences
        const val KEY_AUTH_STATUS = "auth_status" // Ключ для статуса авторизации
        const val KEY_EMAIL = "email" // Ключ для email пользователя
    }

    private lateinit var db: FirebaseFirestore // Firestore для работы с базой данных
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences для хранения данных сессии

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Включаем edge-to-edge отображение
        setContentView(R.layout.activity_main) // Устанавливаем макет
        setupWindowInsets() // Настраиваем отступы для системных баров

        // Инициализация Firestore и SharedPreferences
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        // Проверка автологина
        if (isUserLoggedIn()) {
            navigateToMainActivity2()
            return
        }

        // Настройка кнопок
        setupSignUpButton()
        setupLoginButton()
    }

    // Настройка отступов для системных баров
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Проверка, авторизован ли пользователь
    private fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getString(KEY_AUTH_STATUS, null) == "logged_in"
    }

    // Настройка кнопки регистрации
    private fun setupSignUpButton() {
        findViewById<TextView>(R.id.signupText).setOnClickListener {
            startActivity(Intent(this, SignUp::class.java)) // Переход на экран регистрации
        }
    }

    // Настройка кнопки входа
    private fun setupLoginButton() {
        findViewById<ConstraintLayout>(R.id.button).setOnClickListener {
            val email = findViewById<EditText>(R.id.email).text.toString().trim()
            val password = findViewById<EditText>(R.id.password).text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Введите email и пароль") // Проверка на пустые поля
                return@setOnClickListener
            }

            authenticateUser(email, password) // Аутентификация пользователя
        }
    }

    // Аутентификация пользователя
    private fun authenticateUser(email: String, password: String) {
        db.collection("users")
            .whereEqualTo("email", email) // Поиск пользователя по email
            .whereEqualTo("password", password) // Поиск пользователя по паролю
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (!task.result!!.isEmpty) {
                        // Если пользователь найден, сохраняем данные в SharedPreferences
                        saveUserSession(email)
                        navigateToMainActivity2()
                    } else {
                        showToast("Неверный email или пароль") // Ошибка аутентификации
                    }
                } else {
                    showToast("Ошибка подключения: ${task.exception?.message}") // Ошибка сети или Firestore
                }
            }
    }

    // Сохранение данных сессии
    private fun saveUserSession(email: String) {
        sharedPreferences.edit().apply {
            putString(KEY_AUTH_STATUS, "logged_in")
            putString(KEY_EMAIL, email)
            apply()
        }
    }

    // Переход на MainActivity2
    private fun navigateToMainActivity2() {
        startActivity(Intent(this, MainActivity2::class.java))
        finish() // Закрытие текущей активности
    }

    // Показ Toast-сообщений
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}