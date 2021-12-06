package br.com.lucas.caefadergs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.lucas.caefadergs.Model.TicketModel;

public class MainActivity extends AppCompatActivity {

    private Button btnFilaConvencional, btnFilaPrioritaria;
    private String ticketNumber;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    int count;


    TicketModel ticket = new TicketModel();

    SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy");
    Date data = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* OBTER VALORES DA ACTIVITY */
        btnFilaConvencional = findViewById(R.id.btnFilaConvencional);
        btnFilaPrioritaria = (Button) findViewById(R.id.btnFilaPrioritaria);

        btnFilaConvencional.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                countTickets();
                ticketNumber = pushNewTicket("Normal");
                Intent intent = new Intent(MainActivity.this, QueueActivity.class);
                intent.putExtra("Type", "N");
                intent.putExtra("Ticket", ticketNumber);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        btnFilaPrioritaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countTickets();
                ticketNumber = pushNewTicket("Priority");
                Intent intent = new Intent(MainActivity.this, QueueActivity.class);
                intent.putExtra("Type", "P");
                intent.putExtra("Ticket", ticketNumber);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


    }


    public String pushNewTicket(String typeOfTicket) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();


        reference.child("Tickets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    count++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ticket.setStatus("Criado");
        ticket.setCreatedAt(dataFormatada.format(data));
        ticket.setType(typeOfTicket);
        ticket.setAttendent("");
        ticket.setId(String.valueOf(countTickets()));

        reference.child("Tickets").push().setValue(ticket);

        return ticket.getId();


    }

    private int countTickets (){
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();


        reference.child("Tickets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    count++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return count;
    }

    protected void onStart(){
        super.onStart();
        countTickets();
    }


}