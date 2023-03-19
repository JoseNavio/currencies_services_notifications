package me.josena.currencies_services_notifications.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import me.josena.currencies_services_notifications.data.Currency
import me.josena.currencies_services_notifications.databinding.ActivityCurrencyConverterBinding
import me.josena.currencies_services_notifications.utils.CurrencyParser
import me.josena.currencies_services_notifications.utils.DownloadCurrencyService
import kotlin.math.roundToInt

class CurrencyConverterActivity : AppCompatActivity(), TextWatcher {

    //Currencies
    private lateinit var dollarCurrency: Currency
    private lateinit var euroCurrency: Currency

    //Views
    private lateinit var binding: ActivityCurrencyConverterBinding
    private lateinit var radioEuro: RadioButton
    private lateinit var radioDollar: RadioButton
    private lateinit var labelEuros: TextView
    private lateinit var labelDollars: TextView
    private lateinit var labelResult: TextView
    private lateinit var fieldEuro: EditText
    private lateinit var fieldDollar: EditText
    private lateinit var buttonConvert: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //Binding
        binding = ActivityCurrencyConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Load currencies
        val currencyParser = CurrencyParser(applicationContext);
        val currencies = currencyParser.obtainCurrencies()
        dollarCurrency = currencies[0]
        euroCurrency = currencies[1]

        Log.d("Monedas", "Dolar " + dollarCurrency.name)
        Log.d("Monedas", "Dolar " + dollarCurrency.value)
        Log.d("Monedas", "Euro " + euroCurrency.name)
        Log.d("Monedas", "Euro " + euroCurrency.value)

        initializeViews();

        //Text watcher
        fieldEuro.addTextChangedListener(this)
        fieldDollar.addTextChangedListener(this)

        //Disable
        buttonConvert.isEnabled = false
        enableEuros()

        //Button listener
        buttonConvert.setOnClickListener {

            if (radioEuro.isChecked) {
                labelResult.text = convert(euroCurrency) + " $"
            }
            if (radioDollar.isChecked) {
                labelResult.text = convert(dollarCurrency) + " €"
            }

            Toast.makeText(this, "Ya no es necesario usar este botón.", Toast.LENGTH_SHORT).show()
        }
    }

    //Radio button listener
    fun onRadioButtonClicked(view: View) {
        //Erase last result
        labelResult.text = ""

        if (view is RadioButton) {
            // Is the button now checked?
            //val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                radioEuro.id -> {
                    Log.d("CurrencyActivity", "Euro activated")
                    enableEuros()
                    val filled: Boolean = binding.fieldEuros.text.toString().isNotEmpty()
                    if (filled) {
                        //Enable - Disable button
                        labelResult.text = convert(euroCurrency) + " $"
                    }
                    buttonConvert.isEnabled = filled
                }
                radioDollar.id -> {
                    Log.d("CurrencyActivity", "Dolar activated")
                    enableDollars()
                    val filled: Boolean = binding.fieldDollars.text.toString().isNotEmpty()
                    if (filled) {
                        //Enable - Disable button
                        labelResult.text = convert(dollarCurrency) + " €"
                    }
                    buttonConvert.isEnabled = filled
                }
            }
        }
    }

    //Currency conversion
    fun convert(currency: Currency): String {

        var value: Double
        var finalValue: Double

        if (currency.name.equals("Euro")) {
            value = binding.fieldEuros.text.toString().toDouble()
            finalValue = (value * dollarCurrency.value) / currency.value
        } else if (currency.name.equals("Dolar")) {
            value = binding.fieldDollars.text.toString().toDouble()
            finalValue = (value * euroCurrency.value) / currency.value
        } else {
            finalValue = 0.0
        }
        //Rounded
        val rounded = (finalValue * 100.0).roundToInt() / 100.0
        return rounded.toString()
    }

    //Lateinit - Initialize onStart better?
    fun initializeViews() {
        radioEuro = binding.radioEuros
        radioDollar = binding.radioDolars
        labelDollars = binding.labelDolars
        labelEuros = binding.labelEuros
        labelResult = binding.labelResult
        fieldEuro = binding.fieldEuros
        fieldDollar = binding.fieldDollars
        buttonConvert = binding.buttonConvert
    }

    //Enable - Disable
    fun enableEuros() {

        fieldDollar.isVisible = false
        labelDollars.isVisible = false
        fieldEuro.isVisible = true
        labelEuros.isVisible = true
    }

    fun enableDollars() {

        fieldDollar.isVisible = true
        labelDollars.isVisible = true
        fieldEuro.isVisible = false
        labelEuros.isVisible = false
    }

    //Text watcher implemented methods
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        Log.d("CurrencyActivity", "Before")
        //Erase last result
        labelResult.text = ""
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        Log.d("CurrencyActivity", "On")
    }

    //Update currency exchange while writing...
    override fun afterTextChanged(p0: Editable?) {

        Log.d("CurrencyActivity", "After")
        if (radioEuro.isChecked) {
            val filled: Boolean = binding.fieldEuros.text.toString().isNotEmpty()
            if (filled) {
                labelResult.text = convert(euroCurrency) + " $"
            }
            buttonConvert.isEnabled = filled
        } else {
            val filled: Boolean = binding.fieldDollars.text.toString().isNotEmpty()
            if (filled) {
                labelResult.text = convert(dollarCurrency) + " €"
            }
            buttonConvert.isEnabled = filled
        }
    }
}

//Storage value & name
//enum class Currency {
//
//    EURO(1f), DOLLAR(1.04f);
//
//    val value: Float
//
//    constructor(value: Float) {
//
//        this.value = value
//    }
//}