package com.example.basiccalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.basiccalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        const val MATH_SYMBOLS = ".+-*/,%"
    }

    private lateinit var binding: ActivityMainBinding
    private var calculatorData = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCalculatorData(calculatorData)

        binding.buttonCalculatorClear.setOnClickListener {
            setCalculatorData(insertion = "0", isClearCall = true)
        }

        binding.buttonCalculatorInvertValue.setOnClickListener {
            invertNumber()
            setCalculatorData(insertion = "")
        }

        binding.buttonCalculatorEqual.setOnClickListener {
            //TODO RESULT OF SUM HERE

            calculatorData = (calculatorData.toIntOrNull() ?: 0).toString()

            setCalculatorData(insertion = "")
        }

        binding.buttonCalculatorPercentage.setOnClickListener {
            setCalculatorData(insertion = "%")
        }

        binding.buttonCalculatorAddition.setOnClickListener {
            setCalculatorData(insertion = "+")
        }

        binding.buttonCalculatorMinus.setOnClickListener {
            setCalculatorData(insertion = "-")
        }

        binding.buttonCalculatorDivision.setOnClickListener {
            setCalculatorData(insertion = "/")
        }

        binding.buttonCalculatorMultiply.setOnClickListener {
            setCalculatorData(insertion = "*")
        }

        binding.buttonCalculatorDot.setOnClickListener {
            setCalculatorData(insertion = ".", isDotCall = true)
        }

        binding.buttonCalculatorZero.setOnClickListener {
            setCalculatorData(insertion = "0")
        }

        binding.buttonCalculatorNine.setOnClickListener {
            setCalculatorData(insertion = "9")
        }

        binding.buttonCalculatorEight.setOnClickListener {
            setCalculatorData(insertion = "8")
        }

        binding.buttonCalculatorSeven.setOnClickListener {
            setCalculatorData(insertion = "7")
        }

        binding.buttonCalculatorSix.setOnClickListener {
            setCalculatorData(insertion = "6")
        }

        binding.buttonCalculatorFive.setOnClickListener {
            setCalculatorData(insertion = "5")
        }

        binding.buttonCalculatorFour.setOnClickListener {
            setCalculatorData(insertion = "4")
        }

        binding.buttonCalculatorThree.setOnClickListener {
            setCalculatorData(insertion = "3")
        }

        binding.buttonCalculatorTwo.setOnClickListener {
            setCalculatorData(insertion = "2")
        }

        binding.buttonCalculatorOne.setOnClickListener {
            setCalculatorData(insertion = "1")
        }
    }

    private fun setCalculatorData(
        insertion: String,
        isDotCall: Boolean = false,
        isClearCall: Boolean = false
    ) {
        val hasSymbolAtLast = hasSymbolAtLast(calculatorData) && !isClearCall
        val hasNumberAtLast = hasNumberAtLast(calculatorData) && !isClearCall
        val isSymbolInput = isSymbolInput(insertion)

        /*
        * se número na última posição deve permitir mais numero ou simbolo
        * se simbolo na última posição deve permitir apenas número
        *
        *
        * */
        if (isSymbolInput && !hasSymbolAtLast) {
            calculatorData += insertion
            calculatorData = replaceLeftZeroNumber(isDotCall)
            binding.textCalculatorData.text = calculatorData
        } else if (!isSymbolInput && (hasNumberAtLast || hasSymbolAtLast)) {
            calculatorData += insertion
            calculatorData = replaceLeftZeroNumber(isDotCall)
            binding.textCalculatorData.text = calculatorData
        } else if (isClearCall) {
            calculatorData = "0"
            binding.textCalculatorData.text = calculatorData
        }
    }

    private fun replaceLeftZeroNumber(isDotCall: Boolean): String {
        val needToDropLeftZero = calculatorData.take(1) == "0" && calculatorData.length > 1
        val isZeroWithFloatingPoint = calculatorData.take(2).contains(".") && calculatorData.length > 2

        if (needToDropLeftZero && !isZeroWithFloatingPoint && !isDotCall) {
            return calculatorData.drop(1)
        }

        return calculatorData
    }

    private fun invertNumber() {
        val isPositiveValue = calculatorData.take(1) != "-"
        val isLeftZeroValueWithFloatingPoint = calculatorData.take(2) == "0."
        val isLeftZeroValue = calculatorData.take(1) == "0"
        val hasSymbolAtFirst = hasSymbolAtFirst(calculatorData)
        val isNegativeValueAndHasSymbolAtFirst = !isPositiveValue && hasSymbolAtFirst
        val invertZeroWithFloatingPoint = isPositiveValue && isLeftZeroValueWithFloatingPoint && isLeftZeroValue && !hasSymbolAtFirst
        val invertIntegerValue = isPositiveValue && !isLeftZeroValueWithFloatingPoint && !isLeftZeroValue && !hasSymbolAtFirst

        if (isNegativeValueAndHasSymbolAtFirst) {
            calculatorData = calculatorData.replace("-", "")
        } else if (invertZeroWithFloatingPoint || invertIntegerValue) {
            calculatorData = calculatorData.reversed().plus("-").reversed()
        }
    }

    private fun hasSymbolAtFirst(calculatorData: String): Boolean {
        for (character in MATH_SYMBOLS.iterator()) {
            if (calculatorData.first() == character) {
                return true
            }
        }
        return false
    }

    private fun hasSymbolAtLast(calculatorData: String): Boolean {
        for (character in MATH_SYMBOLS.iterator()) {
            if (calculatorData.last() == character) {
                return true
            }
        }
        return false
    }

    private fun hasNumberAtLast(calculatorData: String): Boolean {
        for (character in MATH_SYMBOLS.iterator()) {
            if (calculatorData.last() != character) {
                return true
            }
        }
        return false
    }

    private fun isSymbolInput(insertion: String): Boolean {
        if (insertion.isNotBlank()) {
            for (character in MATH_SYMBOLS.iterator()) {
                if (insertion.last() == character) {
                    return true
                }
            }
        }
        return false
    }
}