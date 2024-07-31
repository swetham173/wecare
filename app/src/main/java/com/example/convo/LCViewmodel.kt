    package com.example.convo

    import android.app.Application
    import android.content.Context
    import android.net.Uri
    import android.util.Log
    import android.widget.Toast
    import androidx.compose.runtime.mutableStateOf
    import androidx.lifecycle.AndroidViewModel
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import com.example.convo.Data.*
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.*
    import com.google.firebase.storage.FirebaseStorage
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.tasks.await
    import java.util.*
    import java.util.Collections.emptyList
    import javax.inject.Inject

    @HiltViewModel
class LCViewmodel @Inject constructor(

    application: Application,
    var auth: FirebaseAuth,
    var db:FirebaseFirestore,
    var stor:FirebaseStorage
): AndroidViewModel(application) {

        private val context = getApplication<Application>().applicationContext
        private val sharedPreferences =
            context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        private val _userType = MutableLiveData<String>()
        val userType: LiveData<String> get() = _userType
        private val _doctors = MutableLiveData<List<DOCTORINFO>>()
        val doctors: LiveData<List<DOCTORINFO>> get() = _doctors

        private val _chatMessages = MutableLiveData<List<Message>>() //existing msg
        val chatMessages: LiveData<List<Message>> get() = _chatMessages


        private val _sentMessage = MutableLiveData<String?>() //current msg
        val sentMessage: LiveData<String?> = _sentMessage
        private val _recipientId = MutableLiveData<String?>()
        val recipientId: LiveData<String?> = _recipientId


        private val _receivedMessage = MutableLiveData<String?>()
        val receivedMessage: LiveData<String?> = _receivedMessage //current msg  but in rec end


        private var chatMessagesListener: ListenerRegistration? = null
        private val _patientsWithMessage =
            MutableLiveData<List<USERDATA>>()//patients (who contacted doc) and their hist
        val patientsWithMessage: LiveData<List<USERDATA>> get() = _patientsWithMessage


        private val _notes = MutableLiveData<List<Note>>()
        val notes: LiveData<List<Note>> get() = _notes

        private val _images = MutableLiveData<List<Uri>>()
        val images: LiveData<List<Uri>> get() = _images


        val PATIENT_NODE = "Patient"
        val PATIENTS_INSIDE_DOC = "Patients_inside_doc"
        var inProcess = mutableStateOf(false)
        var inSignUp = mutableStateOf(false)
        var userData = mutableStateOf<USERDATA?>(null)
        var doctorData = mutableStateOf<DOCTORINFO?>(null)
        val DOCTOR_NODE = "Doctor"
        val MESSAGE = "Message"
        val CONVERSATION = "Conversations"




        init {

            var currentUser = auth.currentUser
            inSignUp.value = currentUser != null
            val uid = currentUser?.uid
            if (uid != null) {
                db.collection("Patient").document(uid).get()
                    .addOnSuccessListener { patientSnapshot ->
                        if (patientSnapshot.exists()) {
                            // User is a patient
                            getUserId(uid, "Patient")
                        } else {
                            // User is not a patient, check if they are a doctor
                            db.collection("Doctor").document(uid).get()
                                .addOnSuccessListener { doctorSnapshot ->
                                    if (doctorSnapshot.exists()) {
                                        // User is a doctor
                                        getUserId(uid, "Doctor")
                                    } else {
                                        // User is neither a patient nor a doctor
                                        Log.e("init", "User is neither a patient nor a doctor")
                                    }
                                }.addOnFailureListener { exception ->
                                    Log.e("init", "Failed to check user type", exception)
                                }
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("init", "Failed to check user type", exception)
                    }
            }
            fetchCurrentUserType()
            fetchDoctors()

        }

        fun fetchDoctors() {

            try {
                // Accessing a Firestore database collection named DOCTOR_NODE
                db.collection(DOCTOR_NODE).get()
                    .addOnSuccessListener { documents ->
                        // Successfully retrieved documents from Firestore
                        // Convert Firestore documents to a list of DOCTORINFO objects
                        val doctorList = documents.toObjects<DOCTORINFO>()
                        // Update the value of _doctors LiveData with the fetched list
                        _doctors.value = doctorList
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors that occurred while fetching data
                        Log.e("fetchDoctors", "Error getting documents: ", exception)
                    }
            } catch (e: Exception) {
                // Catch any exceptions that may occur during the operation
                Log.e("fetchDoctors", "Exception: ", e)
            }

        }

        private fun fetchCurrentUserType() {
            val storedUserType = sharedPreferences.getString("user_type", null)
            if (storedUserType != null) {
                _userType.value = storedUserType
            } else {
                val currentUser = auth.currentUser
                inSignUp.value = currentUser != null //setting signup val true if cur user not null
                val uid = currentUser?.uid
                if (uid != null) {
                    db.collection("Patient").document(uid).get()
                        .addOnSuccessListener { patientSnapshot ->
                            if (patientSnapshot.exists()) {
                                getUserId(uid, "Patient")
                            } else {
                                db.collection("Doctor").document(uid).get()
                                    .addOnSuccessListener { doctorSnapshot ->
                                        if (doctorSnapshot.exists()) {
                                            getUserId(uid, "Doctor")
                                        } else {
                                            Log.e("init", "User is neither a patient nor a doctor")
                                        }
                                    }.addOnFailureListener { exception ->
                                        Log.e("init", "Failed to check user type", exception)
                                    }
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("init", "Failed to check user type", exception)
                        }
                }
                Log.d("LCViewmodel", "Current User UID: $uid")
                _userType.value = "InitialValue"
            }
        }

        fun setUserType(type: String) {
            _userType.value = type
            sharedPreferences.edit().putString("user_type", type).apply()
            Log.d("ViewModel", "User type set to: $type")
            logUserType()
        }

        fun logUserType() {
            Log.d("LCViewModel", "Current user type: ${_userType.value}")
        }


        fun signUp(name: String, number: String, email: String, password: String, mChoice: String) {
            fun String.isBlank(): Boolean {
                return this == ""
            }

            if (name.isBlank() || number.isBlank() || email.isBlank() || password.isBlank() || mChoice.isBlank()) {
                exceptionHandler(customMsg = "Please enter your details")
                Toast.makeText(context, "Please enter your details", Toast.LENGTH_SHORT).show()

                return
            }
            inProcess.value = true
            db.collection(PATIENT_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
                if (it.isEmpty) {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            inSignUp.value = true
                            // Conditionally store user data based on choice
                            if (mChoice == "Patient") {
                                createOrUpdatePatientProfile(name = name, number = number, mChoice)
                                setUserType(mChoice)
                            } else if (mChoice == "Doctor") {
                                createOrUpdateDoctorProfile(
                                    name = name,
                                    number = number,
                                    mChoice = mChoice
                                )
                                setUserType(mChoice)
                            }
                        } else {

                            exceptionHandler(customMsg = "Sign up Failed!!!")
                        }
                    }

                } else {
                    exceptionHandler(customMsg = "Number already exist")
                    Toast.makeText(context, "Number already exist", Toast.LENGTH_SHORT).show()
                    inProcess.value = false
                    return@addOnSuccessListener
                }
            }
        }

        fun loginIn(email: String, password: String) {
            fun String.isBlank(): Boolean {
                return this == ""
            }

            if (email.isBlank() || password.isBlank()) {
                exceptionHandler(customMsg = "Please enter your details")
                Toast.makeText(context, "Please enter your details", Toast.LENGTH_SHORT).show()

                return
            } else {
                inProcess.value = true
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        inSignUp.value = true
                        inProcess.value = false


                        var currentUser = auth.currentUser
                        if (currentUser != null) {
                            var uid = currentUser.uid
                            if (uid != null) {
                                db.collection("Patient").document(uid).get()
                                    .addOnSuccessListener { patientSnapshot ->
                                        if (patientSnapshot.exists()) {
                                            getUserId(uid, "Patient")
                                            Log.d("LoginIn", "User is a patient")
                                        } else {
                                            db.collection("Doctor").document(uid).get()
                                                .addOnSuccessListener { patientSnapshot ->
                                                    getUserId(uid, "Doctor")
                                                    Log.d("LoginIn", "User is a doctor")
                                                }.addOnFailureListener { exception ->
                                                    exceptionHandler(
                                                        exception,
                                                        "Failed to check user type"
                                                    )
                                                }
                                        }
                                    }.addOnFailureListener { exception ->
                                        exceptionHandler(exception, "Failed to check user type")
                                    }

                            } else {
                                inProcess.value = false
                                exceptionHandler(it.exception, customMsg = "Login failure")
                                Toast.makeText(
                                    context,
                                    "Incorrect email or password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    } else {
                        inProcess.value = false
                        exceptionHandler(it.exception, customMsg = "login failure")
                        Toast.makeText(context, "Incorrect user or password", Toast.LENGTH_SHORT)
                            .show()
                        return@addOnCompleteListener
                    }

                }
            }
        }

        fun createOrUpdatePatientProfile(
            name: String? = null,
            number: String? = null,
            imageURL: String? = null,
            mChoice: String? = null
        ) {
            var uid = auth.currentUser?.uid
            var userDataToBeInserted = USERDATA(
                uid = uid,
                name = name ?: userData.value?.name,  //create or update with same
                number = number ?: userData.value?.number,
                imageURL = imageURL ?: userData.value?.imageURL,
                UserChoice = mChoice ?: userData.value?.UserChoice
            )
            if (uid != null) {
                Log.d("Swe", "Vard")
                inProcess.value = true
                val patRef = db.collection(DOCTOR_NODE).document(uid)
                patRef.get().addOnSuccessListener {
                    if (!it.exists()) {
                        db.collection(PATIENT_NODE).document(uid).set(userDataToBeInserted)
                            .addOnSuccessListener {

                                getUserId(uid, "Patient")
                                Log.d("Nil", "varles")
                                Toast.makeText(context, "Successful ", Toast.LENGTH_SHORT).show()


                            }
                            .addOnFailureListener { exception ->
                                Log.d("Tagg", exception.toString())
                                exceptionHandler(exception, "Cannot retrieve data")
                            }
                    } else {
                        inProcess.value = true
                        //if the doc exist update the current one
                        patRef.update(

                            "name", name ?: it.getString("name"),
                            "imageURL", imageURL ?: it.getString("imageURL"),

                            ).addOnSuccessListener {
                            getUserId(uid, "Doctor")

                            Log.d("LCViewmodel", "Patient profile updated successfully")
                            Toast.makeText(context, "Successful ", Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener { exception ->
                            Log.e("LCViewmodel", "Failed to update PAtient profile", exception)
                            exceptionHandler(exception, "Cannot retrieve data")
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.d("Varle", exception.toString())
                    exceptionHandler(exception, "Cannot retrieve data")
                }


            }
        }

        fun createOrUpdateDoctorProfile(
            name: String? = null,
            number: String? = null,
            imageURL: String? = null,
            mChoice: String? = null,
            descri: String? = null
        ) {
            var uid = auth.currentUser?.uid
            var userDataToBeInserted = DOCTORINFO(
                duid = uid,
                name = name ?: doctorData.value?.name,  //create or update with same
                number = number ?: doctorData.value?.number,
                imageURL = imageURL ?: doctorData.value?.imageURL,
                UserChoice = mChoice ?: doctorData.value?.UserChoice,
                description = descri ?: doctorData.value?.description ?: ""
            )
            if (uid != null) {
                Log.d("Swe", "Vard")
                inProcess.value = true
                val docRef = db.collection(DOCTOR_NODE).document(uid)

                docRef.get().addOnSuccessListener {
                    if (!it.exists()) {
                        db.collection(DOCTOR_NODE).document(uid).set(userDataToBeInserted)
                            .addOnSuccessListener {

                                getUserId(uid, "Doctor")
                                Toast.makeText(context, "Successful ", Toast.LENGTH_SHORT).show()

                                Log.d("LCViewmodel", "Doctor profile updated successfully")
                            }.addOnFailureListener { exception ->
                                Log.e("LCViewmodel", "Failed to update doctor profile", exception)
                                exceptionHandler(exception, "Cannot retrieve data")
                            }
                    } else {
                        inProcess.value = true
                        //if the doc exist update the current one
                        docRef.update(

                            "name", name ?: it.getString("name"),
                            "imageURL", imageURL ?: it.getString("imageURL"),

                            "description", descri ?: it.getString("description") ?: ""
                        ).addOnSuccessListener {
                            getUserId(uid, "Doctor")

                            Log.d("LCViewmodel", "Doctor profile updated successfully")
                            Toast.makeText(context, "Successful ", Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener { exception ->
                            Log.e("LCViewmodel", "Failed to update doctor profile", exception)
                            exceptionHandler(exception, "Cannot retrieve data")
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.e("LCViewmodel", "Failed to retrieve doctor document", exception)
                    exceptionHandler(exception, "Cannot retrieve data")
                }


            }
        }

        fun getUserId(uid: String, userType: String) {
            val collection = if (userType == "Patient") {
                "Patient"
            } else {
                "Doctor"
            }
            db.collection(collection).document(uid).addSnapshotListener { value, error ->
                if (error != null) {
                    exceptionHandler(error, "Couldn't retrieve the user data")
                }
                if (value != null) {
                    if (userType == "Patient") {
                        val user = value.toObject<USERDATA>() // Convert to USERDATA instance
                        userData.value = user // Update userData state
                        setUserType("Patient")
                    } else if (userType == "Doctor") {
                        val doctor = value.toObject<DOCTORINFO>()
                        // Convert to DOCTORINFO instance
                        doctorData.value = doctor// Update doctorData state if needed
                        setUserType("Doctor")
                        Log.d("getUserId", "User type set to Doctor")

                    }
                }
            }
        }

        fun exceptionHandler(exception: Exception? = null, customMsg: String? = "") {
            val errorMsg = exception?.localizedMessage ?: ""

            val msg = customMsg ?: errorMsg //condition
            Log.e("LiveChat", msg, exception)
            exception?.printStackTrace()
            inProcess.value = false
        }

        fun uploadProfileImage(uri: Uri) {
            uploadImage(uri) {
                createOrUpdateDoctorProfile(imageURL = it.toString())
            }
        }

        fun uploadPatProfileImage(uri: Uri) {
            Log.d("Allin", "Good")
            uploadImage(uri) {
                createOrUpdatePatientProfile(imageURL = it.toString())
            }
        }

        fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
            var storageRef = stor.reference
            val uuid = UUID.randomUUID()
            val imageRef = storageRef.child("images/$uuid") //create node
            val uploadTask = imageRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                val result = it?.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
            }.addOnFailureListener {
                exceptionHandler(it, it.toString())
            }
        }

        fun logout() {
            Log.d("Logout", "Logging out...")

            if (auth.currentUser != null) {
                auth.signOut()
                Log.d("Logout", "User signed out")
            }
            inSignUp.value = false
            _userType.value = null
            userData.value = null
            doctorData.value = null
            sharedPreferences.edit().remove("user_type").apply()
            Log.d("Logout", "Local data cleared")
        }

        fun onSendReply(
            chatId: String,
            message: String,
            patientID: String,
            callback: (Boolean) -> Unit
        ) {
            val current = auth.currentUser?.uid ?: ""
            val time = System.currentTimeMillis()
            val recipientId = if (userType.value == "Doctor") patientID else chatId
            _recipientId.value = recipientId
            val msg = Message(current, message, time.toString(), recipientId)

            // Reference to the conversations collection
            val conversationsRef = db.collection(CONVERSATION)

            // Query to check if a conversation with the same userId and doctorId already exists
            val query = conversationsRef
                .whereEqualTo("userId", patientID)
                .whereEqualTo("doctorId", chatId)

            query.get().addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Create a new conversation document if it doesn't exist
                    val newConversation = Conversation(
                        userId = patientID,
                        doctorId = chatId,
                        lastMessage = message,
                        timestamp = time.toString()
                    )
                    conversationsRef.add(newConversation)
                        .addOnSuccessListener { documentReference ->
                            _sentMessage.value = message
                            callback(true)
                            Log.d("onSendReply", "Conversation successfully created!")

                            // Add the message to the subcollection
                            documentReference.collection(MESSAGE).add(msg)
                                .addOnSuccessListener {
                                    _receivedMessage.value = msg.toString()
                                    callback(true)
                                    Log.d("onSendReply", "Message successfully sent!")

                                }
                                .addOnFailureListener { exception ->
                                    callback(false)
                                    Log.e("onSendReply", "Error sending message", exception)
                                }
                        }
                        .addOnFailureListener { exception ->
                            callback(false)
                            Log.e("onSendReply", "Error creating conversation", exception)
                        }
                } else {
                    // Update the existing conversation document
                    val existingConversationDoc = querySnapshot.documents[0].reference
                    existingConversationDoc.update(
                        "lastMessage", message,
                        "timestamp", time
                    ).addOnSuccessListener {
                        _sentMessage.value = message
                        callback(true)
                        Log.d("onSendReply", "Conversation successfully updated!")

                        // Add the message to the subcollection
                        existingConversationDoc.collection(MESSAGE).add(msg)
                            .addOnSuccessListener {
                                _receivedMessage.value = msg.toString()
                                callback(true)
                                Log.d("onSendReply", "Message successfully sent!")

                            }
                            .addOnFailureListener { exception ->
                                callback(false)
                                Log.e("onSendReply", "Error sending message", exception)
                            }
                    }.addOnFailureListener { exception ->
                        callback(false)
                        Log.e("onSendReply", "Error updating conversation", exception)
                    }
                }
            }.addOnFailureListener { exception ->
                callback(false)
                Log.e("onSendReply", "Error checking conversation existence", exception)
            }
        }

        fun fetchChatMessages(doctorId: String, patientId: String) {
            // Reference to the conversations collection
            val conversationsRef = db.collection(CONVERSATION)

            // Query to find the specific conversation between the doctor and patient
            val query = conversationsRef
                .whereEqualTo("userId", patientId)
                .whereEqualTo("doctorId", doctorId)

            query.get().addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    // Get the reference to the first (and should be only) document
                    val conversationDoc = querySnapshot.documents[0].reference

                    // Set up listener for real-time updates in the MESSAGE subcollection
                    chatMessagesListener = conversationDoc.collection(MESSAGE)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, exception ->
                            if (exception != null) {
                                Log.e("fetchChatMessages", "Listen failed: ", exception)
                                return@addSnapshotListener
                            }

                            if (snapshot != null && !snapshot.isEmpty) {
                                val messages = snapshot.toObjects<Message>()
                                _chatMessages.postValue(messages)
                            } else {
                                Log.d("fetchChatMessages", "No messages found")
                            }
                        }
                } else {
                    Log.d(
                        "fetchChatMessages",
                        "No conversation found between this doctor and patient"
                    )
                }
            }.addOnFailureListener { exception ->
                Log.e("fetchChatMessages", "Error finding conversation: ", exception)
            }
        }

        fun fetchPatientWithMessage() {
            val currentDoctorId = auth.currentUser?.uid
            if (currentDoctorId != null) {
                db.collection(CONVERSATION)
                    .whereEqualTo("doctorId", currentDoctorId)
                    .get()
                    .addOnSuccessListener { documents ->
                        val patientIds = ArrayList<String>()
                        for (document in documents) {
                            val userId = document.getString("userId")
                            if (userId != null) {
                                patientIds.add(userId)
                            }
                        }
                        if (!patientIds.isEmpty()) {
                            fetchPatients(patientIds)
                        } else {
                            Log.d(
                                "fetchPatientWithMessage",
                                "No patient IDs found for current doctor"
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "fetchPatientsWithMessages",
                            "Error getting conversations: ",
                            exception
                        )
                    }
            } else {
                Log.e("fetchPatientsWithMessages", "Current doctor ID is null")
            }
        }

        private fun fetchPatients(patientIds: List<String>) {
            if (!patientIds.isEmpty()) {
                db.collection(PATIENT_NODE)
                    .whereIn("uid", patientIds)
                    .get()
                    .addOnSuccessListener { patientDocuments ->
                        val patients = patientDocuments.toObjects<USERDATA>()
                        _patientsWithMessage.value = patients
                    }
                    .addOnFailureListener { exception ->
                        Log.e("fetchPatientsWithMessages", "Error getting patients: ", exception)
                    }
            } else {
                Log.d("fetchPatientsWithMessages", "Empty or null patientIds list")
            }
        }

        //sus=waiting without disturbing the async function and later resuming to do their work based on what main task had done....tht is waiting to count how many datatypes are placed
        suspend fun fetchDoctor(chatId: String): DOCTORINFO? {
            Log.d("fetchDoctor", "Fetching doctor for chatId: $chatId")
            return try {
                val snapshot = db.collection(DOCTOR_NODE)
                    .whereEqualTo("duid", chatId)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val doctorInfo = snapshot.documents[0].toObject<DOCTORINFO>()
                    Log.d("fetchDoctor", "Doctor fetched successfully: ${doctorInfo?.name}")
                    doctorInfo
                } else {
                    Log.d("fetchDoctor", "No doctor found for chatId: $chatId")
                    null
                }
            } catch (e: Exception) {
                Log.e("fetchDoctor", "Error fetching doctor for chatId: $chatId", e)
                null
            }
        }

        suspend fun fetchPatient(patientID: String): USERDATA? {
            Log.d("fetchPatient", "Fetching patient for patientID: $patientID")
            return try {
                val snapshot = db.collection(PATIENT_NODE)
                    .whereEqualTo("uid", patientID)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val userData = snapshot.documents[0].toObject<USERDATA>()
                    Log.d("fetchPatient", "Patient fetched successfully: ${userData?.name}")
                    userData
                } else {
                    Log.d("fetchPatient", "No patient found for patientID: $patientID")
                    null
                }
            } catch (e: Exception) {
                Log.e("fetchPatient", "Error fetching patient for patientID: $patientID", e)
                null
            }
        }

        fun clearSentMessage() {
            _sentMessage.value = null
        }

        fun addNote(title: String, content: String, userId: String, noteId: String) {
            val currentUser = auth.currentUser?.uid
            val time = System.currentTimeMillis().toString()
            if (currentUser != null) {
                val note = Note(
                    noteId = noteId,
                    title = title,
                    content = content,
                    timestamp = time
                )
                Log.d("LCViewmodel", "Current User: $currentUser")
                Log.d("LCViewmodel", "Note ID: $noteId")
                val userNoteRef =
                    db.collection("notes").document(userId).collection("TD").document(noteId)
                userNoteRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Update existing note
                            userNoteRef.set(note)
                                .addOnSuccessListener {
                                    Log.d("LCViewmodel", "Note updated successfully")
                                    fetchNotes() // Refresh the notes after updating the note
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("LCViewmodel", "Error updating note", exception)
                                }
                        } else {
                            // Add new note
                            userNoteRef.set(note)
                                .addOnSuccessListener {
                                    Log.d("LCViewmodel", "Note added successfully")
                                    fetchNotes() // Refresh the notes after adding a new note
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("LCViewmodel", "Error adding note", exception)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("LCViewmodel", "Error checking note existence", exception)
                    }
            }
        }

        fun deleteNote(noteId: String) {
            val currentUser = auth.currentUser?.uid
            if (currentUser != null) {
                db.collection("notes")
                    .document(currentUser)
                    .collection("TD")
                    .document(noteId)
                    .delete()
                    .addOnSuccessListener {
                        fetchNotes() // Refresh the notes after deleting a note
                    }
                    .addOnFailureListener { exception ->
                        Log.e("LCViewmodel", "Error deleting note", exception)
                    }
            }
        }


        fun fetchNotes() {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("notes")
                    .document(currentUser.uid)
                    .collection("TD")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            Log.e("LCViewmodel", "Error fetching notes", exception)
                            return@addSnapshotListener
                        }

                        val noteList = ArrayList<Note>()
                        snapshot?.forEach { document ->
                            val note = document.toObject<Note>()
                            noteList.add(note)
                        }
                        _notes.value = noteList
                    }
            }
        }

//memories

fun uploadPhoto(uri: Uri, onResult: (Boolean, Uri?) -> Unit) {
        val storageRef = stor.reference.child("photos/${UUID.randomUUID()}.jpg")

    storageRef.putFile(uri)
        .addOnSuccessListener { uploadTask ->
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                // Store the download URL in Firestore
                val photoData = HashMap<String, Any>()
                photoData.put("url", downloadUri.toString())

                db.collection("photos").add(photoData)
                    .addOnSuccessListener {
                        onResult(true, downloadUri)
                    }
                    .addOnFailureListener {
                        onResult(false, null)
                    }
            }.addOnFailureListener {
                onResult(false, null)
            }
        }.addOnFailureListener {
            onResult(false, null)
        }
}

        fun fetchPhotos(onPhotosFetched: (List<Uri>) -> Unit) {
            db.collection("photos").get()
                .addOnSuccessListener { result ->
                    val photoUris = ArrayList<Uri>()
                    for (document in result) {
                        val url = document.getString("url")
                        if (url != null) {
                            photoUris.add(Uri.parse(url))
                        }
                    }
                    onPhotosFetched(photoUris)
                }
                .addOnFailureListener {
                    // Handle the failure if needed
                    onPhotosFetched(emptyList())
                }
        }


        fun deletePhoto(uri: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
            // Delete from Firestore based on URI match
            db.collection("photos")
                .whereEqualTo("url", uri.toString())
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Delete the document where the URL matches
                        document.reference.delete()
                            .addOnSuccessListener {
                                // Deletion from Firestore succeeded
                                onSuccess.invoke()
                            }
                            .addOnFailureListener { exception ->

                                onFailure.invoke(exception.message ?: "Failed to delete photo from Firestore")
                            }
                    }
                }
                .addOnFailureListener { exception ->

                    onFailure.invoke(exception.message ?: "Failed to query photo from Firestore")
                }
        }

    }

