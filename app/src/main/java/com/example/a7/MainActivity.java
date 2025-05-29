package com.example.a7;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText loginEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private EditText birthDateEditText;
    private Spinner ingredientsSpinner;
    private CheckBox personalDataCheckBox;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        fullNameEditText = findViewById(R.id.fullNameEditText);
        loginEditText = findViewById(R.id.loginEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);
        ingredientsSpinner = findViewById(R.id.ingredientsSpinner);
        personalDataCheckBox = findViewById(R.id.personalDataCheckBox);
        registerButton = findViewById(R.id.registerButton);

        // Настройка выпадающего списка
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.borscht_ingredients_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ingredientsSpinner.setAdapter(adapter);

        // Обработчик для выбора даты
        birthDateEditText.setOnClickListener(v -> showDatePickerDialog());


        // Обработчик кнопки регистрации
        registerButton.setOnClickListener(v -> validateAndRegister());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d.%02d.%d", selectedDay, selectedMonth + 1, selectedYear);
                    birthDateEditText.setText(date);
                }, year, month, day);

        // Установка минимальной даты - 1900 год
        calendar.set(1900, 0, 1);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void validateAndRegister() {
        boolean isValid = true;

        // Валидация ФИО (только кириллица, пробелы и дефисы)
        String fullName = fullNameEditText.getText().toString();
        if (!Pattern.matches("^[А-Яа-яЁё\\s-]+$", fullName)) {
            fullNameEditText.setError(getString(R.string.error_field));
            isValid = false;
        }

        // Валидация логина (только латиница)
        String login = loginEditText.getText().toString();
        if (!Pattern.matches("^[A-Za-z]+$", login)) {
            loginEditText.setError(getString(R.string.error_field));
            isValid = false;
        }

        // Валидация email
        String email = emailEditText.getText().toString();
        if (!Pattern.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
            emailEditText.setError(getString(R.string.error_field));
            isValid = false;
        }

        // Валидация пароля
        String password = passwordEditText.getText().toString();
        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.error_field));
            isValid = false;
        }

        // Проверка совпадения паролей
        String repeatPassword = repeatPasswordEditText.getText().toString();
        if (!password.equals(repeatPassword)) {
            repeatPasswordEditText.setError(getString(R.string.error_password_mismatch));
            isValid = false;
        }

        // Проверка даты рождения
        if (birthDateEditText.getText().toString().isEmpty()) {
            birthDateEditText.setError(getString(R.string.error_field));
            isValid = false;
        }

        // Проверка согласия
        if (!personalDataCheckBox.isChecked()) {
            Toast.makeText(this, R.string.error_checkbox, Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
            // Здесь можно добавить логику сохранения данных
        }
    }

    // Класс для форматирования номера телефона
    private class PhoneTextWatcher implements TextWatcher {
        private boolean isFormatting;
        private boolean deletingHyphen;
        private int hyphenStart;
        private String hyphen = "-";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count == 1 && s.charAt(start) == '-') {
                deletingHyphen = true;
                hyphenStart = start;
            } else {
                deletingHyphen = false;
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isFormatting) {
                return;
            }

            isFormatting = true;

            if (deletingHyphen && hyphenStart < s.length()) {
                s.delete(hyphenStart, hyphenStart + 1);
            }

            // Удаляем все нецифровые символы
            String digits = s.toString().replaceAll("\\D", "");
            StringBuilder formatted = new StringBuilder();

            if (digits.length() >= 3) {
                formatted.append("+").append(digits.substring(0, 3));
                if (digits.length() > 3) {
                    formatted.append(digits.substring(3));
                }
            } else {
                formatted.append("+").append(digits);
            }

            s.replace(0, s.length(), formatted.toString());

            isFormatting = false;
        }
    }
}