package fr.uparis.chenliu.projet.data.model

class QuestionParameter(
    val question: List<QuestionData>
)

class QuestionData(
    val subject: Int,
    val difficulty: Int,
    val question: String,
    val resources: String?
)

class AnswerParameter(
    val answer: List<AnswerData>
)

class AnswerData(
    val idQuestion: Int,
    val answer: String,
    val validity: Boolean,
    val position: Int?
)