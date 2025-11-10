package fr.uparis.chenliu.projet.data.model


class JsonReader {
    class QuizzParameter(
        val quizz: List<QuizzData>
    )

    class QuizzData(
        val id: Int,
        val type: Int,
        val question: String,
        val propositions: List<List<String>>,
        val answer: String,
        val difficulty: String
    )
}