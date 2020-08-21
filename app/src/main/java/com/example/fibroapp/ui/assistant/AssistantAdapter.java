package com.example.fibroapp.ui.assistant;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fibroapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class AssistantAdapter extends RecyclerView.Adapter<AssistantAdapter.ViewHolder> {
    private static String TAGTTS = "TTS";

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView description;
        public TextToSpeech mTTS;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public ViewHolder(View itemView){
            super(itemView);

            image = itemView.findViewById(R.id.image_assistant);
            description = itemView.findViewById(R.id.text_assistant);
            description.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);

             mTTS = createTextToSpeech();
        }
    }
    private List<Assistant> assistants;
    private Context context;
    ArrayList<TextToSpeech> listTTS;

    public AssistantAdapter(List<Assistant> assistants, Context context){
        this.assistants = assistants;
        this.context = context;
        listTTS = new ArrayList<>();
    }

    @NonNull
    @Override
    public AssistantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View assistantView = inflater.inflate(R.layout.item_assistant, parent, false);

        // Create a new holder instance
        final AssistantAdapter.ViewHolder viewHolder = new ViewHolder(assistantView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assistant a = assistants.get(position);

        //ImageView
        Glide.with(context)
                .load(Uri.parse(a.getPathImage()))
                .into(holder.image);

        //Title
        TextView title = holder.description;
        title.setText(a.getDescription());

        //TextToSpeech
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.mTTS.isSpeaking()){
                    holder.mTTS.speak(a.getDescription(), TextToSpeech.QUEUE_FLUSH, null);
                    listTTS.add(holder.mTTS);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return assistants.size();
    }

    TextToSpeech auxTTS;
    private TextToSpeech createTextToSpeech(){
        auxTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = auxTTS.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e(TAGTTS, "Lengujae not supported");
                    }
                }else{
                    Log.e(TAGTTS, "Initialize failed");
                }
            }
        });

        return auxTTS;
    }

    public void destroyTTS(){
        for (int i = 0; i<listTTS.size(); i++){
            if (listTTS.get(i) != null){
                listTTS.get(i).shutdown();
                listTTS.get(i).stop();
            }
        }
    }
}
