import kotlin.math.abs
import kotlin.random.Random

typealias Matrix = Array<IntArray> // синоним для понятного чтения кода

val nodesList = mutableListOf<Node>()


fun main() {
    val aimField = arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6), intArrayOf(7, 8, 0))

    // исходное состояние: пример
    val field = arrayOf(intArrayOf(0, 3, 2), intArrayOf(8, 1, 5), intArrayOf(6, 4, 7))
    //field = createRandomMatrix(field)
    // создаем массивы для хранения координат целевого состояния
    val dataOfAimNodes = dataOfField(aimField).second.toList()

    println("Original field")
    printMatrix(field)

    nodesList.add(Node(field))

    // алгоритм для поиска оптимального решения
    while (true) {
        var index = minDistance()
        if (isEqual(index, aimField)) {
            // вывод шагов на экран
            val resultList = mutableListOf<Node>()
            resultList.add(nodesList[index])
            index = nodesList[index].indexOfField
            while (index != 0) {
                resultList.add(nodesList[index])
                index = nodesList[index].indexOfField
            }
            resultList.reverse()
            for (node in resultList)
                printMatrix(node.field)
            break
        } else
            selectionOfMove(index, dataOfAimNodes)
    }
}

/** Выбор куда пойдёт 0 */
fun selectionOfMove(
    index: Int,
    dataOfAimNodes: List<Element>,
) {
    val mainHero = dataOfField(nodesList[index].field).first // ячейка 0

    val nodeMoveRight = Node(arrayOf())
    if (mainHero.aim_y < 2) {
        // right
        with(nodeMoveRight) {
            field = right(nodesList[index].field.copy(), mainHero)
            if (isExpandable(field)) {
                indexOfField = index
                distance = sumDistance(field, dataOfAimNodes)
                step = nodesList[index].step + 1
                nodesList.add(this)
            }
        }
    }

    val nodeMoveLeft = Node(arrayOf())
    if (mainHero.aim_y > 0) {
        // left
        with(nodeMoveLeft) {
            field = left(nodesList[index].field.copy(), mainHero)
            if (isExpandable(field)) {
                indexOfField = index
                distance = sumDistance(field, dataOfAimNodes)
                step = nodesList[index].step + 1
                nodesList.add(this)
            }
        }
    }

    val nodeMoveBottom = Node(arrayOf())
    if (mainHero.aim_x < 2) {
        // bottom
        with(nodeMoveBottom) {
            field = bottom(nodesList[index].field.copy(), mainHero)
            if (isExpandable(field)) {
                indexOfField = index
                distance = sumDistance(field, dataOfAimNodes)
                step = nodesList[index].step + 1
                nodesList.add(this)
            }
        }
    }

    val nodeMoveTop = Node(arrayOf())
    if (mainHero.aim_x > 0) {
        // top
        with(nodeMoveTop) {
            field = top(nodesList[index].field.copy(), mainHero)
            if (isExpandable(field)) {
                indexOfField = index
                distance = sumDistance(field, dataOfAimNodes)
                step = nodesList[index].step + 1
                nodesList.add(this)
            }
        }
    }

    nodesList[index].distance = 10000
}

/** Выбирает по наименьшему f, с какого места ему двигаться дальше */
fun minDistance(): Int {
    var f = 10000
    var index = 0
    for (i in nodesList.indices) {
        if (nodesList[i].distance == f) {
            continue
        } else if ((nodesList[i].distance + nodesList[i].step) < f) {
            index = i
            f = nodesList[i].distance + nodesList[i].step
        }
    }
    return index

}

/** Проверка того, что 0 не пойдёт обратно */
fun isExpandable(field: Matrix): Boolean {
    for (i in nodesList.indices) {
        if (isEqual(i, field))
            return false
    }
    return true
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

/** Сравнивает, совпадает ли узел с узлом, на который указывает index */
fun isEqual(index: Int, field: Matrix):Boolean {
    for (row in field) {
        for (element in row)
            if (nodesList[index].field[field.indexOf(row)][row.indexOf(element)] != element)
                return false
    }
    return true
}

/** Функция расширение для копирования двумерного массива */
fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

/** Меняет местами ячейку 0 с другой ячейкой */
fun swap(mainHero: Element, civilian_x: Int, civilian_y: Int, field: Matrix): Matrix {
    val c = field[mainHero.aim_x][mainHero.aim_y]
    field[mainHero.aim_x][mainHero.aim_y] = field[civilian_x][civilian_y]
    field[civilian_x][civilian_y] = c
    return field
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

/** Находит текущую позицию 0 */
fun dataOfField(field: Matrix): Pair<Element, MutableList<Element>> {
    val data = mutableListOf<Element>()
    lateinit var mainHero: Element
    for (row in field) {
        for (element in row) {
            if (element != 0) {
                data += Element(element, field.indexOf(row), row.indexOf(element))
            } else {
                mainHero =
                    Element(element, field.indexOf(row), row.indexOf(element))
            }
        }
    }
    return Pair(mainHero, data)
}

/** Суммарное расстояние до целевых позиций */
fun sumDistance(field: Matrix, dataOfAimNodes: List<Element>): Int {
    val data = dataOfField(field.copy()).second
    var sum = 0
    for (aimNode in dataOfAimNodes)
        for (currentNode in data)
            if (aimNode.elementOfField == currentNode.elementOfField)
                sum += abs(aimNode.aim_x - currentNode.aim_x) + abs(aimNode.aim_y - currentNode.aim_y)
    return sum
}

/** Поворот направо */
fun right(field: Matrix, mainHero: Element): Matrix {
    return swap(mainHero, mainHero.aim_x, mainHero.aim_y + 1, field)
}

/** Поворот налево */
fun left(field: Matrix, mainHero: Element): Matrix {
    return swap(mainHero, mainHero.aim_x, mainHero.aim_y - 1, field)
}

/** Подъем */
fun top(field: Matrix, mainHero: Element): Matrix {
    return swap(mainHero, mainHero.aim_x - 1, mainHero.aim_y, field)
}

/** Спуск */
fun bottom(field: Matrix, mainHero: Element): Matrix {
    return swap(mainHero, mainHero.aim_x + 1, mainHero.aim_y, field)
}

