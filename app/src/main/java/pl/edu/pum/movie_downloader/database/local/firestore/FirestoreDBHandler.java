package pl.edu.pum.movie_downloader.database.local.firestore;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.edu.pum.movie_downloader.activities.NavHostActivity;
import pl.edu.pum.movie_downloader.models.DownloadHistoryDTO;

public class FirestoreDBHandler {
    private final FirebaseFirestore db;
    private final String TAG = "FIRESTORE STATUS";

    public FirestoreDBHandler() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void add(String userName, String id, String title, String format, String downloadDate, String source) {
        DownloadHistoryDTO data = new DownloadHistoryDTO(title, downloadDate, format, source);
        //it is create a new collection if doesnt exist; if exist just add data to collection
        db.collection(userName)
                .document(id)
                .set(data.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "DocumentSnapshot successfully written");
                })
                .addOnFailureListener(e ->  Log.w(TAG, "Error writing document", e));
    }

    public List<DownloadHistoryDTO> getAllData(String userName, FirestoreState firestoreState) {
        List<DownloadHistoryDTO> result = new ArrayList<>();
        db.collection(userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String id = document.getId();
                            String title = (String) document.get("title");
                            String format = (String) document.get("format");
                            String source = (String) document.get("source");
                            String date = (String) document.get("download_date");
                            DownloadHistoryDTO data = new DownloadHistoryDTO(title, date, format, source);
                            data.setID(id); //it will be usefull during delete
                            result.add(data);
                        }
                        firestoreState.isOperationSuccessfully("SUCCESS");
                    } else {
                        firestoreState.isOperationSuccessfully("NO_SUCCESS");
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
        return result;
    }

    public void deleteAllData(String userName, String id) {
        db.collection(userName)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    //firestoreState.isOperationSuccessfully("DELETED");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting document", e);
                    //firestoreState.isOperationSuccessfully("NO_DELETED");
                });
    }
}
