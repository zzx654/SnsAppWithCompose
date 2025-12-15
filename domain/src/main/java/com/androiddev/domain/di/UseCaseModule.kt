package com.androiddev.domain.di

import android.content.Context
import com.androiddev.domain.repository.signup.AuthPhoneRepository
import com.androiddev.domain.repository.postdetail.CommentRepository
import com.androiddev.domain.repository.createprofile.CreateProfileRepository
import com.androiddev.domain.repository.fcm.FcmRepository
import com.androiddev.domain.repository.notification.NotificationRepository
import com.androiddev.domain.repository.postlist.GetPostsRepository
import com.androiddev.domain.repository.postdetail.PostRepository
import com.androiddev.domain.repository.signin.SigninRepository
import com.androiddev.domain.repository.signup.SignupRepository
import com.androiddev.domain.repository.postdetail.ToggleLikePostRepository
import com.androiddev.domain.repository.tag.TagRepository
import com.androiddev.domain.repository.uploadpost.UploadPostRepository
import com.androiddev.domain.repository.postdetail.VoteRepository
import com.androiddev.domain.use_case.signup.authphone.AuthPhoneUseCases
import com.androiddev.domain.use_case.signup.authphone.AuthenticateCode
import com.androiddev.domain.use_case.postdetail.CancelVote
import com.androiddev.domain.use_case.createprofile.CheckNickname
import com.androiddev.domain.use_case.postdetail.CommentUseCases
import com.androiddev.domain.use_case.createprofile.CreateProfile
import com.androiddev.domain.use_case.createprofile.CreateProfileUseCases
import com.androiddev.domain.use_case.fcm.FcmTokenUseCase
import com.androiddev.domain.use_case.notification.DeleteNotifications
import com.androiddev.domain.use_case.notification.GetNotifications
import com.androiddev.domain.use_case.notification.NotificationUseCases
import com.androiddev.domain.use_case.notification.ReadAllNotifications
import com.androiddev.domain.use_case.notification.ReadNotification
import com.androiddev.domain.use_case.postdetail.DeletePost
import com.androiddev.domain.use_case.uploadpost.EditPost
import com.androiddev.domain.use_case.signin.EmailSignIn
import com.androiddev.domain.use_case.signup.emailsignup.EmailSignUp
import com.androiddev.domain.use_case.signup.emailsignup.EmailSignUpUseCases
import com.androiddev.domain.use_case.postdetail.GetComments
import com.androiddev.domain.use_case.postlist.GetNearPosts
import com.androiddev.domain.use_case.postdetail.GetPopularComments
import com.androiddev.domain.use_case.postlist.GetNewPosts
import com.androiddev.domain.use_case.postlist.GetNewTagPosts
import com.androiddev.domain.use_case.postlist.GetPopularTagPosts
import com.androiddev.domain.use_case.postlist.GetPostsUseCases
import com.androiddev.domain.use_case.reply.GetReplies
import com.androiddev.domain.use_case.postdetail.GetSelectedComment
import com.androiddev.domain.use_case.postlist.GetSelectedPost
import com.androiddev.domain.use_case.postdetail.GetVoteInfo
import com.androiddev.domain.use_case.postdetail.PostComment
import com.androiddev.domain.use_case.postdetail.PostDetailUseCases
import com.androiddev.domain.use_case.reply.PostReply
import com.androiddev.domain.use_case.reply.ReplyUseCases
import com.androiddev.domain.use_case.tag.GetTags
import com.androiddev.domain.use_case.signup.emailsignup.RequestEmailAuthCode
import com.androiddev.domain.use_case.signup.authphone.RequestPhoneAuthCode
import com.androiddev.domain.use_case.tag.SearchTag
import com.androiddev.domain.use_case.signin.SignInUseCases
import com.androiddev.domain.use_case.signin.SignInWithToken
import com.androiddev.domain.use_case.signin.SocialSignIn
import com.androiddev.domain.use_case.signup.socialsignup.SocialSignUpUseCase
import com.androiddev.domain.use_case.postdetail.ToggleLikeComment
import com.androiddev.domain.use_case.postdetail.ToggleLikePost
import com.androiddev.domain.use_case.tag.TagUseCases
import com.androiddev.domain.use_case.tag.ToggleFavoriteTag
import com.androiddev.domain.use_case.uploadpost.UploadPost
import com.androiddev.domain.use_case.uploadpost.UploadPostUseCases
import com.androiddev.domain.use_case.postdetail.Vote
import com.androiddev.domain.use_case.postdetail.VoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideSignInUseCases(repository: SigninRepository): SignInUseCases {
        return SignInUseCases(
            socialSignIn = SocialSignIn(repository),
            emailSignIn = EmailSignIn(repository),
            signInWithToken = SignInWithToken(repository)
        )
    }
    @Provides
    @Singleton
    fun provideAuthPhoneUseCases(repository: AuthPhoneRepository, @ApplicationContext context: Context): AuthPhoneUseCases {
        return AuthPhoneUseCases(
            requestAuthCode = RequestPhoneAuthCode(repository,context),
            authenticateCode = AuthenticateCode(repository)
        )
    }
    @Provides
    @Singleton
    fun provideSocialSignUpUseCase(repository: SignupRepository): SocialSignUpUseCase {
        return SocialSignUpUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideEmailSignUpUseCase(repository: SignupRepository, @ApplicationContext context: Context): EmailSignUpUseCases {
        return EmailSignUpUseCases(
            requestAuthCode = RequestEmailAuthCode(repository,context),
            emailSignUp = EmailSignUp(repository)
        )
    }
    @Provides
    @Singleton
    fun provideCreateProfileUseCases(repository: CreateProfileRepository): CreateProfileUseCases {
        return CreateProfileUseCases(
            createProfile = CreateProfile(repository),
            checkNickname = CheckNickname(repository)
        )
    }
    @Provides
    @Singleton
    fun provideUploadPostUseCases(uploadRepository: UploadPostRepository, tagRepository: TagRepository): UploadPostUseCases {
        return UploadPostUseCases(
            searchTag = SearchTag(tagRepository),
            uploadPost = UploadPost(uploadRepository),
            editPost = EditPost(uploadRepository)
        )
    }
    @Provides
    @Singleton
    fun provideGetPostsUseCases(repository: GetPostsRepository): GetPostsUseCases {
        return GetPostsUseCases(
            getNearPosts = GetNearPosts(repository),
            getSelectedPost = GetSelectedPost(repository),
            getNewPosts = GetNewPosts(repository),
            getPopularTagPosts = GetPopularTagPosts(repository),
            getNewTagPosts = GetNewTagPosts(repository)
        )
    }
    @Provides
    @Singleton
    fun provideTagUseCases(repository: TagRepository): TagUseCases {
        return TagUseCases(
            getTags = GetTags(repository),
            searchTag = SearchTag(repository),
            toggleFavoriteTag = ToggleFavoriteTag(repository)
        )
    }
    @Provides
    @Singleton
    fun providePostDetailUseCases(
        toggleLikePostRepository: ToggleLikePostRepository,
        postRepository: PostRepository
    ): PostDetailUseCases {
        return PostDetailUseCases(
            ToggleLikePost = ToggleLikePost(toggleLikePostRepository),
            DeletePost = DeletePost(postRepository)
        )
    }
    @Provides
    @Singleton
    fun provideCommentUseCases(
        commentRepository: CommentRepository
    ): CommentUseCases {
        return CommentUseCases(
            GetSelectedComment = GetSelectedComment(commentRepository),
            GetComments = GetComments(commentRepository),
            GetPopularComments = GetPopularComments(commentRepository),
            PostComment = PostComment(commentRepository),
            ToggleLikeComment = ToggleLikeComment(commentRepository)
        )
    }
    @Provides
    @Singleton
    fun provideReplyUseCases(
        commentRepository: CommentRepository
    ): ReplyUseCases {
        return ReplyUseCases(
            GetReplies = GetReplies(commentRepository),
            PostReply = PostReply(commentRepository)
        )
    }
    @Provides
    @Singleton
    fun provideVoteUseCases(
        voteRepository: VoteRepository
    ): VoteUseCases {
        return VoteUseCases(
            getVoteInfo = GetVoteInfo(voteRepository),
            vote = Vote(voteRepository),
            cancelVote = CancelVote(voteRepository)
        )
    }
    @Provides
    @Singleton
    fun provideFcmUseCase(
        fcmRepository: FcmRepository
    ): FcmTokenUseCase {
        return FcmTokenUseCase(fcmRepository)
    }
    @Provides
    @Singleton
    fun provideNotificationUseCaseS(
        notificationRepository: NotificationRepository
    ): NotificationUseCases {
        return NotificationUseCases(
            getNotifications = GetNotifications(notificationRepository),
            readAllNotifications = ReadAllNotifications(notificationRepository),
            deleteNotifications = DeleteNotifications(notificationRepository),
            readNotification = ReadNotification(notificationRepository)
        )
    }
}