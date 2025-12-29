package fr.uparis.chenliu.projet.data.model


class JsonReader {
    class QuizzParameter(
        val quizz: List<QuizzData>
    )

    class QuizzData(
        val type: Int,
        val question: String,
        val propositions: List<List<String>>,
        val answer: List<String>,
        val difficulty: String,
        val subject: Int
    )

    class SubjectParameter(
        val subject: List<SubjectData>
    )

    class SubjectData(
        val name: String,
        val imageName: String
    )

    class DecoParameter(
        val deco: List<DecoData>
    )

    class DecoData(
        val name: String,
        val path: String,
        val type: Int,
        val diff: Int
    )
}