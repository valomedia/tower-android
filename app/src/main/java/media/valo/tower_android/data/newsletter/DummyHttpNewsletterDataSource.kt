package media.valo.tower_android.data.newsletter

class DummyHttpNewsletterDataSource: NewsletterDataSource {

    override suspend fun subscribe() = Unit

}