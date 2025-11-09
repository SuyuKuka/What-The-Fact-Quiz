package fr.uparis.chenliu.projet.data.model


class JsonReader {
    class QuizzParameter(
        val quizz: List<QuizzData>
    )

    class QuizzData(
        val id: Int,
        val question: String,
        val propositions: List<String>,
        val answer: String,
        val difficulty: String
    )
}