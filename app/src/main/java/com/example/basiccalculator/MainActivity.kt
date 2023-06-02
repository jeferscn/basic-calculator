package com.example.basiccalculator

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.example.basiccalculator.databinding.ActivityMainBinding
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    companion object {
        const val MATH_SYMBOLS = ".+-*/%"
        const val MATH_SYMBOLS_FORBIDDEN_AT_FIRST = ".+*/%"
    }

    private lateinit var binding: ActivityMainBinding
    private var calculatorData = "0"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        setCalculatorData(calculatorData)

        binding.buttonCalculatorClear.setOnClickListener {
            setCalculatorData(insertion = "0", isClearCall = true)
        }

        binding.buttonCalculatorInvertValue.setOnClickListener {
            invertNumber()
            setCalculatorData(insertion = "")
        }

        binding.buttonCalculatorEqual.setOnClickListener {
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
        val hasSymbolAtLast = (hasSymbolAtLast(calculatorData) || takeLastSymbol(calculatorData) == ".") && !isClearCall
        val hasNumberAtLast = hasNumberAtLast(calculatorData) && !isClearCall
        val isSymbolInput = isSymbolInput(insertion)
        val isPercentageFirstSymbolInput = takeFirstSymbol(calculatorData) == "%"

        val isAllowedSymbolChange = isAllowedSymbolChange(insertion, calculatorData) &&
                !checkInput(insertion, "%") &&
                !hasSymbolAtFirst(calculatorData) &&
                takeFirstSymbol(calculatorData).isNotBlank() ||
                isDotLastSymbol() && !checkInput(insertion, "%") ||
                !checkInput(insertion, "%") &&
                takeFirstSymbol(calculatorData) == "%"

        val isAllowedOnlyNumberInput = isSymbolInput &&
                takeFirstSymbol(calculatorData).isNotBlank() &&
                !hasSymbolAtLast

        val isAllowedNumberAndSymbolInput = hasNumberAtLast ||
                hasSymbolAtLast

        // Control the input flow to calculatorData
        if (!isClearCall && (isAllowedSymbolChange || isAllowedNumberAndSymbolInput || isAllowedOnlyNumberInput)) {
            if (isAllowedSymbolChange) {
                if (!checkInput(insertion, "%") && takeFirstSymbol(calculatorData) != "%") {
                    calculatorData = calculatorData.replace(calculatorData.last().toString(), insertion)
                }
            } else {
                calculatorData = solveMathEquation(insertion, isEqualCall)
                if (!checkInput(insertion, "%")) {
                    calculatorData += insertion
                }
            }

            calculatorData = replaceLeftZeroNumber(isDotCall)

            binding.textCalculatorData.text = calculatorData
        } else if (isClearCall) {

            calculatorData = "0"

            binding.textCalculatorData.text = calculatorData
        }

        //Remove the first input if it's forbidden symbol
        removeForbiddenSymbolsAtFirst(calculatorData)
    }

    private fun solveMathEquation(insertion: String, isEqualCall: Boolean): String {

        //Verify if the call is coming from math operation symbols or from equals
        val isAllowedToCalculate =
            takeFirstSymbol(calculatorData).isNotBlank() &&
                    !checkInput(insertion, ".") &&
                    !hasForbiddenSymbolAtFirst(calculatorData) ||
                    !checkInput(insertion, "%") &&
                    takeFirstSymbol(calculatorData) != "%"

        val activateResultBySymbolInput =
            isSymbolInputToGetResult(insertion, 2) &&
                    !hasSymbolAtFirst(calculatorData) ||
                    isSymbolInputToGetResult(insertion, 3) &&
                    hasSymbolAtFirst(calculatorData)

        if (isAllowedToCalculate && (activateResultBySymbolInput || isEqualCall)) {

            // Symbol removal if value is negative
            var isNegativeValue = false
            if (calculatorData.first() == '-') {
                calculatorData = calculatorData.drop(1)
                isNegativeValue = true
            }

            val firstMathSymbol = takeFirstSymbol(calculatorData)
            val valuesList = calculatorData.split(firstMathSymbol)

            runCatching {
                if (insertion == "%" && takeFirstSymbol(calculatorData) != "%") {

                    return percentageEquation(
                        valuesList[0].toDouble(),
                        valuesList[1].removePercentageAtLast()
                    ).decimalZerosRemover().addNegativeSymbolAtFirst(isNegativeValue)

                } else {

                    val result = when (firstMathSymbol) {
                        "+" -> "${valuesList[0].toDouble() + valuesList[1].toDouble()}"
                        "-" -> "${valuesList[0].toDouble() - valuesList[1].toDouble()}"
                        "*" -> "${valuesList[0].toDouble() * valuesList[1].toDouble()}"
                        "/" -> "${valuesList[0].toDouble() / valuesList[1].toDouble()}"
                        else -> ""
                    }

                    return result.decimalZerosRemover().addNegativeSymbolAtFirst(isNegativeValue)

                }
            }.onFailure {
                errorMessage()
            }
        }
        return calculatorData
    }

    private fun percentageEquation(value1: Double, value2: String): String {
        val string = value2.decimalZerosRemover()

        val result = when (takeFirstSymbol(calculatorData)) {
            "+" -> "${value1 + (value1 * (string.toDouble() / 100))}"
            "-" -> "${value1 - (value1 * (string.toDouble() / 100))}"
            "*" -> "${value1 * (string.toDouble() / 100)}"
            "/" -> "${value1 / (string.toDouble() / 100)}"
            else -> ""
        }

        return result
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
        val invertZeroWithFloatingPoint =
            isPositiveValue && isLeftZeroValueWithFloatingPoint && isLeftZeroValue && !hasSymbolAtFirst
        val invertIntegerValue =
            isPositiveValue && !isLeftZeroValueWithFloatingPoint && !isLeftZeroValue && !hasSymbolAtFirst

        if (isNegativeValueAndHasSymbolAtFirst) {
            calculatorData = calculatorData.replace("-", "")
        } else if (invertZeroWithFloatingPoint || invertIntegerValue) {
            calculatorData = calculatorData.reversed().plus("-").reversed()
        }
    }

    private fun removeForbiddenSymbolsAtFirst(calculatorData: String) {
        if (hasSymbolAtFirst(calculatorData) && calculatorData.length < 2) {
            this@MainActivity.calculatorData = "0"
            binding.textCalculatorData.text = this@MainActivity.calculatorData
        }
    }

    private fun String.addNegativeSymbolAtFirst(isNegativeValue: Boolean): String {
        if (isNegativeValue) {
            return this.reversed().plus("-").reversed()
        }

        return this
    }

    private fun String.removePercentageAtLast(): String {
        if (this.last() == '%') {
            this.dropLast(1)
        }
        return this
    }

    private fun hasForbiddenSymbolAtFirst(calculatorData: String): Boolean {
        for (character in MATH_SYMBOLS_FORBIDDEN_AT_FIRST.iterator()) {
            if (calculatorData.first() == character) {
                return true
            }
        }
        return false
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

    private fun checkInput(insertion: String, checkInsertionToThis: String): Boolean {
        if (insertion.isNotBlank()) {
            if (insertion.last().toString() == checkInsertionToThis) {
                return true
            }
        }
        return false
    }


    private fun isSymbolInputToGetResult(insertion: String, countUntilSymbol: Int = 0): Boolean {
        var symbolsCount = 0
        for (character in MATH_SYMBOLS.iterator()) {
            for (symbol in calculatorData.iterator()) {
                if (symbol == character && !isDotFirstSymbol()) {
                    symbolsCount++
                }
            }
        }

        if (isSymbolInput(insertion = insertion) && !isDotFirstSymbol()) {
            symbolsCount++
        }

        if (symbolsCount >= countUntilSymbol) {
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

    private fun takeLastSymbol(calculatorData: String): String {
        if (calculatorData.isNotBlank()) {
            for (character in MATH_SYMBOLS.iterator()) {
                if (calculatorData.last() == character) {
                    return character.toString()
                }
            }
        }
        return ""
    }

    private fun String.decimalZerosRemover(): String {
        runCatching {
            val decimalFormat = DecimalFormat("0.#")
            decimalFormat.roundingMode = RoundingMode.UNNECESSARY
            return decimalFormat.format(this.toDouble()).toString()
        }
        return this
    }

    private fun isDotFirstSymbol(): Boolean {
        return takeFirstSymbol(calculatorData) == "."
    }

    private fun isDotLastSymbol(): Boolean {
        return takeLastSymbol(calculatorData) == "."
    }

    private fun errorMessage() {
        Toast.makeText(this, "Entrada inválida, tente novamente!", Toast.LENGTH_SHORT).show()
        calculatorData = "Error"
        binding.textCalculatorData.text = calculatorData
    }
}