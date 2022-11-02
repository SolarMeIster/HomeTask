import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

typealias Matrix = Array<IntArray> // синоним для понятного чтения кода

var step = 0

/**
 * Игра в восемь
 *
 * Предполагается, что мы будем управлять ячейкой 0
 * В данном случае ячейка 0 - это пустая клетка
 *
 *
 * */
fun main() {
    val aimField = arrayOf(intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8))
    var field = arrayOf<IntArray>()
    field = createRandomMatrix(field)
    var triple: Triple<Matrix, MutableList<Node>, Node>? = null
    // создаем массивы для хранения координат целевого состояния и текщего состояния
    var pair = dataOfDistance(aimField)
    val dataOfAimNodes = pair.first.toList()
    pair = dataOfDistance(field)
    var dataOfCurrentNodes = pair.first

    var mainHero = pair.second // ячейка 0

    println(dataOfCurrentNodes)
    println(dataOfAimNodes)
    println(sumDistance(dataOfCurrentNodes, dataOfAimNodes))

    printMatrix(field)
    println(selectionOfMove(field, dataOfAimNodes, mainHero))
 //while (i++ < 6) {
     /*if (mainHero.aim_y < 2) mainHero = right(currentField, mainHero).second

     printMatrix(currentField)

     if (mainHero.aim_y > 0) mainHero = left(currentField, mainHero).second

     printMatrix(currentField)

     if (mainHero.aim_x < 2) mainHero = bottom(currentField, mainHero).second

     printMatrix(currentField)

     if (mainHero.aim_x > 0) mainHero = top(currentField, mainHero).second

     printMatrix(currentField)*/

 //}

    // алгоритм для поиска оптимального решения
    /*while (true) {
        when (selectionOfMove(currentField, dataOfCurrentNodes, dataOfAimNodes, mainHero)) {
            Movement.RIGHT -> {
                triple = right(currentField, mainHero)
                currentField = triple.first
                dataOfCurrentNodes = triple.second
                mainHero = triple.third
            }
            Movement.LEFT -> {
                triple = left(currentField, mainHero)
                currentField = triple.first
                dataOfCurrentNodes = triple.second
                mainHero = triple.third
            }
            Movement.TOP -> {
                triple = top(currentField, mainHero)
                currentField = triple.first
                dataOfCurrentNodes = triple.second
                mainHero = triple.third
            }
            Movement.BOTTOM -> {
                triple = bottom(currentField, mainHero)
                currentField = triple.first
                dataOfCurrentNodes = triple.second
                mainHero = triple.third
            }
            else -> println("Нет решения!")
        }

        printMatrix(currentField)

        if (dataOfCurrentNodes == dataOfAimNodes) {
            break
        }
    }*/

    printMatrix(aimField)

}

/** Вывод массива на экран */
fun printMatrix(field: Matrix) {
    for (set in field) {
        for (value in set) {
            print("$value ")
        }
        println()
    }
    println()
}

/** Выбор куда пойдёт 0 */
fun selectionOfMove(
    field: Matrix,
    dataOfAimNodes: List<Node>,
    mainHero: Node
): Movement {
    var f = 10000
    var currentField: Matrix = field
    var dataOfCurNodes: MutableList<Node>
    var result: Int
    var movement = Movement.EMPTY
    var currF: Int
    step++
    if (mainHero.aim_y < 2) {
        // поворот направо
        currentField = right(currentField, mainHero).first
        dataOfCurNodes = dataOfDistance(currentField).first
        result = sumDistance(dataOfCurNodes, dataOfAimNodes)
        currF = result + step
        println("Right $result\n")
        printMatrix(currentField)
        println()
        if (currF < f)  movement = Movement.RIGHT
        f = min(f, currF)
    }
    currentField = field
    if (mainHero.aim_y > 0) {
        // поворот налево
        currentField = left(currentField, mainHero).first
        dataOfCurNodes = dataOfDistance(currentField).first
        result = sumDistance(dataOfCurNodes, dataOfAimNodes)
        currF = result + step
        println("Left $result\n")
        printMatrix(currentField)
        println()
        if (currF < f) movement = Movement.LEFT
        f = min(f, currF)
    }
    currentField = field
    if (mainHero.aim_x < 2) {
        // поворот вниз
        currentField = bottom(currentField, mainHero).first
        dataOfCurNodes = dataOfDistance(currentField).first
        result = sumDistance(dataOfCurNodes, dataOfAimNodes)
        currF = result + step
        println("Bottom $result\n")
        printMatrix(currentField)
        println()
        if (currF < f) movement = Movement.BOTTOM
        f = min(f, currF)
    }
    currentField = field
    if (mainHero.aim_x > 0) {
        //поворот вверх
        currentField = top(currentField, mainHero).first
        dataOfCurNodes = dataOfDistance(currentField).first
        result = sumDistance(dataOfCurNodes, dataOfAimNodes)
        currF = result + step
        println("Top $result\n")
        printMatrix(currentField)
        println()
        if (currF < f) movement = Movement.TOP
    }
    return movement
}

/** Меняет местами ячейку 0 с другой ячейкой */
fun swap(mainHero: Node, civilian_x: Int, civilian_y: Int, field: Matrix): Pair<Matrix, Node> {
    val c = field[mainHero.aim_x][mainHero.aim_y]
    field[mainHero.aim_x][mainHero.aim_y] = field[civilian_x][civilian_y]
    field[civilian_x][civilian_y] = c
    val mainHeroNew = Node(mainHero.elementOfField, civilian_x, civilian_y)
    return Pair(field, mainHeroNew)
}

/** Заполняем двумерный массив */
fun createRandomMatrix(field: Matrix): Matrix {
    var newField = field
    for (i in 0..2) {
        val set = mutableSetOf<Int>()
        for (j in 0..2) {
            when (i) {
                0 -> set.add(rand(0, 2, set))
                1 -> set.add(rand(3, 5, set))
                else -> set.add(rand(6, 8, set))
            }
        }
        newField += set.toIntArray()
    }
    newField = shuffleMatrix(newField)
    return newField
}

/** Генерирует случайные числа в строках Set (Set - массив, который хранит в себе только уникальные элементы) */
fun rand(start: Int, end: Int, set: MutableSet<Int>): Int {
    var flag = false
    var ran = 0
    while (!flag) {
        ran = Random.nextInt(start, end + 1)
        if (!set.contains(ran)) {
            flag = true
        }
    }
    return ran
}

/** Перетасовка двумерного массива */
fun shuffleMatrix(field: Matrix): Matrix {
    var colons = intArrayOf()
    var shuffleField = listOf<IntArray>()
    for (j in field.indices) {
        for (i in field.indices) {
            colons += field[i][j]
        }
        colons.shuffle()
        shuffleField += colons
        colons = intArrayOf()
    }
    shuffleField = shuffleField.shuffled()
    return shuffleField.toTypedArray()
}

/** Создает список координат всех элементов на текущий момент */
fun dataOfDistance(field: Matrix): Pair<MutableList<Node>, Node> {
    val data = mutableListOf<Node>()
    lateinit var mainHero: Node
    for (row in field)
        for (element in row)
            if (element != 0) {
                data += Node(element, field.indexOf(row), row.indexOf(element))
            } else {
                mainHero =
                    Node(element, field.indexOf(row), row.indexOf(element))
            }
    return Pair(data, mainHero)
}

/** Суммарное расстояние до целевых позиций */
fun sumDistance(dataOfCurrentNodes: MutableList<Node>, dataOfAimNodes: List<Node>): Int {
    var sum = 0
    for (aimNode in dataOfAimNodes)
        for (currentNode in dataOfCurrentNodes)
            if (aimNode.elementOfField == currentNode.elementOfField)
                sum += abs(aimNode.aim_x - currentNode.aim_x) + abs(aimNode.aim_y - currentNode.aim_y)
    return sum
}

/** Поворот направо */
fun right(field: Matrix, mainHero: Node): Pair<Matrix, Node> {
    return swap(mainHero, mainHero.aim_x, mainHero.aim_y + 1, field)
}

/** Поворот налево */
fun left(field: Matrix, mainHero: Node): Pair<Matrix, Node> {
    return swap(mainHero, mainHero.aim_x, mainHero.aim_y - 1, field)
}

/** Подъем */
fun top(field: Matrix, mainHero: Node): Pair<Matrix, Node> {
    return swap(mainHero, mainHero.aim_x - 1, mainHero.aim_y, field)
}

/** Спуск */
fun bottom(field: Matrix, mainHero: Node): Pair<Matrix, Node> {
    return swap(mainHero, mainHero.aim_x + 1, mainHero.aim_y, field)
}

