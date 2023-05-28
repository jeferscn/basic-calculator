package com.example.basiccalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

            setCalculatorData(insertion = "", isEqualCall = true)
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
        isClearCall: Boolean = false,
        isEqualCall: Boolean = false,
    ) {
        val hasSymbolAtLast = hasSymbolAtLast(calculatorData) && !isClearCall
        val hasNumberAtLast = hasNumberAtLast(calculatorData) && !isClearCall
        val isSymbolInput = isSymbolInput(insertion)
        val isAllowedSymbolChange = isAllowedSymbolChange(insertion, calculatorData) &&
                !hasSymbolAtFirst(calculatorData) &&
                takeFirstSymbol(calculatorData).isNotBlank() &&
                !isDotCall
        val isAllowedOnlyNumberInput = isSymbolInput && takeFirstSymbol(calculatorData).isNotBlank() && !hasSymbolAtLast
        val isAllowedNumberAndSymbolInput = hasNumberAtLast || hasSymbolAtLast

        // Control the input flow to calculatorData
        if (isAllowedNumberAndSymbolInput || isAllowedOnlyNumberInput || isAllowedSymbolChange) {
            if (isAllowedSymbolChange) {
                calculatorData = calculatorData.replace(calculatorData.last().toString(), insertion)
            } else {
                calculatorData = solveMathEquation(insertion, isEqualCall)
                calculatorData += insertion
            }
            calculatorData = replaceLeftZeroNumber(isDotCall)
            binding.textCalculatorData.text = calculatorData
        } else if (isClearCall) {
            calculatorData = "0"
            binding.textCalculatorData.text = calculatorData
        }
    }

    private fun solveMathEquation(insertion: String, isEqualCall: Boolean): String {
        if (takeFirstSymbol(calculatorData).isNotBlank() &&
            !isDotInput(insertion) &&
            !isPercentageInput(insertion) &&
            isSecondSymbolInput(insertion) ||
            isEqualCall
        ) {
            val firstMathSymbol = takeFirstSymbol(calculatorData)
            val valuesList = calculatorData.split(firstMathSymbol)

            runCatching {
                val result = when (firstMathSymbol) {
                    "+" -> "${valuesList[0].toInt() + valuesList[1].toInt()}"
                    "-" -> "${valuesList[0].toInt() - valuesList[1].toInt()}"
                    "*" -> "${valuesList[0].toInt() * valuesList[1].toInt()}"
                    "/" -> "${valuesList[0].toInt() / valuesList[1].toInt()}"
                    else -> ""
                }
                return result
            }.onFailure {
                Toast.makeText( this,"Entrada inválida, tente novamente!", Toast.LENGTH_SHORT).show()
            }
        }
        return calculatorData
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

    private fun isDotInput(insertion: String): Boolean {
        if (insertion.isNotBlank()) {
            if (insertion.last() == '.') {
                return true
            }
        }
        return false
    }

    private fun isPercentageInput(insertion: String): Boolean {
        if (insertion.isNotBlank()) {
            if (insertion.last() == '.') {
                return true
            }
        }
        return false
    }

    private fun isSecondSymbolInput(insertion: String): Boolean {

        var symbolsCount = 0
        for (character in MATH_SYMBOLS.iterator()) {
            for (symbol in calculatorData.iterator()) {
                if (symbol == character) {
                    symbolsCount++
                }
            }
        }

        if (isSymbolInput(insertion = insertion)) {
            symbolsCount++
        }

        if (symbolsCount >= 2) {
            return true
        }
        return false
    }

    private fun isAllowedSymbolChange(insertion: String, calculatorData: String): Boolean {
        if (insertion.isNotBlank()) {
            for (character in MATH_SYMBOLS.iterator()) {
                if (calculatorData.last() == character && isSymbolInput(insertion)) {
                    return true
                }
            }
        }
        return false
    }

    private fun takeFirstSymbol(calculatorData: String): String {
        if (calculatorData.isNotBlank()) {
            for (character in MATH_SYMBOLS.iterator()) {
                if (calculatorData.contains(character) && character != '.') {
                    return character.toString()
                }
            }
        }
        return ""
    }
}