package com.example.convo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModules {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
@Provides
fun provideFireStore():FirebaseFirestore{
    return FirebaseFirestore.getInstance()
}
@Provides
fun provideFireStorage():FirebaseStorage{
    return FirebaseStorage.getInstance()
}
}