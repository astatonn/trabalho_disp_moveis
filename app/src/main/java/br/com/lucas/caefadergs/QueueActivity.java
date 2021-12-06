package br.com.lucas.caefadergs;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import br.com.lucas.caefadergs.Model.TicketModel;

public class QueueActivity extends AppCompatActivity {
    private TextView txtPersonalTicket, txtTicketCall, txtAttendent, txtBoardText;
    private ChildEventListener childEventListener;
    private Query query;
    TicketModel ticketFicticio = new TicketModel("Este", "Ticket", "Não","é válido");
    List<TicketModel> QueueList = new ArrayList<>() ;

    SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy");
    Date data = new Date ();



    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        FirebaseDatabase firebaseDatabase;
        DatabaseReference reference;

        Intent intent = getIntent();

        QueueList.add(ticketFicticio);


        txtPersonalTicket = findViewById(R.id.txtPersonalTicket);
        txtPersonalTicket.setText(getIntent().getStringExtra("Ticket")+getIntent().getStringExtra("Type"));


        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();

        query = reference;

        txtTicketCall = findViewById(R.id.ticketCall);


        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                if (snapshot.exists()){
                    for (DataSnapshot snap :  snapshot.getChildren()){

                        if (String.valueOf(snap.child("createdAt").getValue(String.class)).equals(String.valueOf(dataFormatada.format(data)))){

                            if (String.valueOf(snap.child("status").getValue(String.class)).equals("Chamando")){

                                TicketModel ticket = new TicketModel();

                                ticket.setCreatedAt(String.valueOf(snap.child("createdAt").getValue(String.class)) );
                                ticket.setId(String.valueOf(snap.child("id").getValue(String.class)));
                                ticket.setAttendent(String.valueOf( snap.child("attendent").getValue(String.class)) );
                                ticket.setType(String.valueOf( snap.child("type").getValue(String.class) ) );
                                ticket.setStatus(String.valueOf( snap.child("status").getValue(String.class) ) );

                                QueueList.add(ticket);

                            }
                        }
                    }

                }

                carregarTickets();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                QueueList.clear();

                for (DataSnapshot snap : snapshot.getChildren() ){

                    if (snap.child("createdAt").getValue(String.class).equals(dataFormatada.format(data))){

                        if (snap.child("status").getValue(String.class).equals("Chamando")){

                            TicketModel ticket = new TicketModel();

                            ticket.setCreatedAt(snap.child("createdAt").getValue(String.class));
                            ticket.setId(snap.child("id").getValue(String.class));
                            ticket.setAttendent(snap.child("attendent").getValue(String.class));
                            ticket.setType(snap.child("type").getValue(String.class));
                            ticket.setStatus(snap.child("status").getValue(String.class));


                            QueueList.add(ticket);


                        }
                    }
                }
                carregarTickets();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

            query.addChildEventListener( childEventListener );
            findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CancelarTicket(String.valueOf(getIntent().getStringExtra("Ticket")), String.valueOf(getIntent().getStringExtra("Type")));
                }
            });

    }

    private void carregarTickets () {

        txtTicketCall = findViewById(R.id.ticketCall);
        txtAttendent = findViewById(R.id.txtBoard);
        txtBoardText = findViewById(R.id.txtBoardText);

        Thread thread = new Thread(){
            @Override
            public void run (){
                while (true) {

                    runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          if (count >= Integer.valueOf(QueueList.size())) count = 0;
                                          if (String.valueOf(QueueList.size()).equals('0')) {
                                              txtTicketCall.setText("Aguarde");
                                              txtBoardText.setText("Você será chamado");
                                              txtAttendent.setText("em breve");

                                          }
                                          if (QueueList.get(count).getId() != null){
                                              if (String.valueOf(QueueList.get(count).getType()).equals("Normal")) {

                                                  txtTicketCall.setText(String.valueOf(QueueList.get(count).getId())+ "N");


                                              }
                                              else {

                                                  txtTicketCall.setText(QueueList.get(count).getId() + "P");

                                              }
                                              txtAttendent.setText(QueueList.get(count).getAttendent());

                                          }


                                count++;
                        }
                    });
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();



    }

    @Override
    protected void onStop() {
        super.onStop();

        query.removeEventListener(childEventListener);
    }

    public void CancelarTicket (String idTicket, String typeTicket) {
        final FirebaseDatabase[] fbDatabase = new FirebaseDatabase[1];
        final DatabaseReference[] ref = new DatabaseReference[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tem certeza que deseja cancelar sua solicitação?").setIcon(android.R.drawable.ic_menu_delete).setTitle("Cancelar");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fbDatabase[0] = FirebaseDatabase.getInstance();
                ref[0] = fbDatabase[0].getReference().child("Tickets");
                ref[0].addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {

                                if (String.valueOf(snap.child("createdAt").getValue(String.class)).equals(String.valueOf(dataFormatada.format(data)))) {



                                        if (String.valueOf(snap.child("id").getValue(String.class)).equals(idTicket)) {

                                            ref[0].child(snap.getKey()).child("status").setValue("Finalizado");
                                            finish();


                                        }

                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Não faz nada
            }
        });

        builder.show();
    }
}