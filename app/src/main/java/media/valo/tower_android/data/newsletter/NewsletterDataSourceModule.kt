package media.valo.tower_android.data.newsletter

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NewsletterDataSourceModule {

    @Binds
    fun bindNewsletterDataSource(
        httpNewsletterDataSource: HttpNewsletterDataSource
    ): NewsletterDataSource

}
