package at.htlgkr.tournamaker.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.stream.Collectors;

import at.htlgkr.tournamaker.Activities.FragmentsActivity;
import at.htlgkr.tournamaker.Activities.TournamentActivity;
import at.htlgkr.tournamaker.Classes.Benutzer;
import at.htlgkr.tournamaker.R;
import at.htlgkr.tournamaker.Classes.Tournament;
import at.htlgkr.tournamaker.Classes.TournamentAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class searchFragment extends Fragment
{
    private List<Benutzer> allBenutzer = FragmentsActivity.allBenutzer;
    private Benutzer currentBenutzer = FragmentsActivity.currentBenutzer;
    private static List<Tournament> allTournaments = FragmentsActivity.allTournaments;
    private static StorageReference firebaseStorage = FragmentsActivity.firebaseStorage;
    private static DatabaseReference tournamentsDataBase = FragmentsActivity.tournamentsDataBase;
    private View activityView;
    private TournamentAdapter tournamentAdapter;

    public searchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activityView = inflater.inflate(R.layout.fragment_search, container, false);
        ListView tournamentListView = activityView.findViewById(R.id.tournamentsListView);
        bindAdapterToListView(tournamentListView);

        tournamentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Tournament selectedTournament = tournamentAdapter.getItem(position);

                Intent i = new Intent(getContext(), TournamentActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable("selectedTournament", selectedTournament);
                i.putExtra("extra", extras);

                startActivity(i);
            }
        });


        ImageView search = activityView.findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tv_search = ((TextView) activityView.findViewById(R.id.tv_search)).getText().toString();
                List<Tournament> filtered = allTournaments
                        .stream()
                        .filter((t) -> t.getName().equals(tv_search))
                        .collect(Collectors.toList());

                tournamentAdapter = new TournamentAdapter(getContext(), R.layout.listviewitem_layout, filtered);

                tournamentListView.setAdapter(tournamentAdapter);
                tournamentAdapter.notifyDataSetChanged();
            }
        });



        return activityView;
    }

    private void bindAdapterToListView(ListView lv) {
        tournamentAdapter = new TournamentAdapter(getContext(), R.layout.listviewitem_layout, allTournaments);

        lv.setAdapter(tournamentAdapter);
        tournamentAdapter.notifyDataSetChanged();

    }
}
