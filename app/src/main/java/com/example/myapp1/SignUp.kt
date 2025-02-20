package com.example.myapp1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore // Firestore для работы с базой данных
    private lateinit var emailEditText: EditText // Поле для ввода email
    private lateinit var passwordEditText: EditText // Поле для ввода пароля
    private lateinit var signUpButton: ConstraintLayout // Кнопка регистрации
    private lateinit var signInText: TextView // Текст для перехода на экран входа
    private lateinit var progressBar: ProgressBar // Индикатор загрузки

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signupactivity) // Устанавливаем макет

        db = FirebaseFirestore.getInstance() // Инициализация Firestore
        initViews() // Инициализация элементов интерфейса
        setupListeners() // Настройка слушателей
    }

    // Инициализация элементов интерфейса
    private fun initViews() {
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        signUpButton = findViewById(R.id.button)
        signInText = findViewById(R.id.signupText)
        progressBar = findViewById(R.id.progressBar)
    }

    // Настройка слушателей
    private fun setupListeners() {
        // Обработка нажатия на кнопку регистрации
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                checkEmailAndRegister(email, password)
            }
        }

        // Обработка нажатия на текст для перехода на экран входа
        signInText.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Закрываем текущую активность
        }
    }

    // Валидация ввода
    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                showToast("Введите email")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Некорректный формат email")
                false
            }
            password.isEmpty() -> {
                showToast("Введите пароль")
                false
            }
            password.length < 6 -> {
                showToast("Пароль должен быть не менее 6 символов")
                false
            }
            else -> true
        }
    }

    // Проверка email и регистрация
    private fun checkEmailAndRegister(email: String, password: String) {
        showLoading(true) // Показываем индикатор загрузки
        db.collection("users")
            .whereEqualTo("email", email) // Поиск пользователя по email
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.isEmpty) {
                        createNewUser(email, password) // Если email не занят, создаем нового пользователя
                    } else {
                        showLoading(false)
                        showToast("Этот email уже зарегистрирован")
                    }
                } else {
                    showLoading(false)
                    Log.e("FIREBASE_ERROR", "Ошибка проверки email", task.exception)
                    showToast("Ошибка проверки: ${task.exception?.message}")
                }
            }
    }

    // Создание нового пользователя
    private fun createNewUser(email: String, password: String) {
        val user = hashMapOf(
            "email" to email,
            "password" to password
        )

        db.collection("users")
            .add(user) // Добавляем нового пользователя в Firestore
            .addOnSuccessListener { documentReference ->
                Log.d("FIREBASE_SUCCESS", "Документ создан с ID: ${documentReference.id}")
                saveUserSession(email) // Сохраняем данные сессии
                navigateToMainActivity2() // Переход на MainActivity2
                showLoading(false) // Скрываем индикатор загрузки
            }
            .addOnFailureListener { e ->
                showLoading(false)
                Log.e("FIREBASE_ERROR", "Ошибка создания документа", e)
                showToast("Ошибка регистрации: ${e.localizedMessage}")
            }
    }

    // Сохранение данных сессии
    private fun saveUserSession(email: String) {
        val sp = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)
        sp.edit().apply {
            putString(MainActivity.KEY_AUTH_STATUS, "logged_in")
            putString(MainActivity.KEY_EMAIL, email)
            apply()
        }
    }

    // Переход на MainActivity2
    private fun navigateToMainActivity2() {
        startActivity(Intent(this, MainActivity2::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Очистка стека активностей
        })
        finish() // Закрываем текущую активность
    }

    // Показ/скрытие индикатора загрузки
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        signUpButton.isEnabled = !show // Блокируем кнопку регистрации во время загрузки
    }

    // Показ Toast-сообщений
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}