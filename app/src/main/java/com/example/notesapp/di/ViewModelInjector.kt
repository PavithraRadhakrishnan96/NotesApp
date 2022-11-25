package com.example.notesapp.di

import com.example.notesapp.addnote.AddNoteViewModel
import com.example.notesapp.auth.viewmodel.AuthViewModel
import com.example.notesapp.home.HomeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface ViewModelInjector {
    fun inject(authViewModel: AuthViewModel)
    fun inject(homeViewModel: HomeViewModel)
    fun inject(homeViewModel: AddNoteViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector
        fun appModule(appModule: AppModule): Builder
    }
}