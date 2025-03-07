package vn.duy.sqlite;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import vn.duy.sqlite.models.NotesModel;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler databaseHandler;
    ListView listView;
    ArrayList<NotesModel> arrayList;
    NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        anhXa();
        InitDatabaseSQLite();
        createDatabaseSQLite();
        databaseSQLite();

    }
    private void createDatabaseSQLite() {
        // Thêm dữ liệu vào bảng
//        for (int i=1; i<=2; i++) {
//            databaseHandler.QueryData("INSERT INTO Notes VALUES(null, 'Ví dụ SQLite " + i + "')");
//        }
        databaseHandler.QueryData("INSERT INTO Notes VALUES(null, 'Ví dụ SQLite 1')");
        databaseHandler.QueryData("INSERT INTO Notes VALUES(null, 'Ví dụ SQLite 2')");
    }

    private void InitDatabaseSQLite() {
        // Khởi tạo database
        databaseHandler = new DatabaseHandler(this, "notes.sqlite", null, 1);

        // Tạo bảng Notes nếu chưa có
        databaseHandler.QueryData("CREATE TABLE IF NOT EXISTS Notes(Id INTEGER PRIMARY KEY AUTOINCREMENT, NameNotes VARCHAR(200))");
    }

    private void databaseSQLite() {
        arrayList.clear();
        // Lấy dữ liệu từ bảng Notes
        Cursor cursor = databaseHandler.GetData("SELECT * FROM Notes");
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            int id = cursor.getInt(0);
            arrayList.add(new NotesModel(id, name));
           // Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

    private void anhXa() {
        listView = (ListView) findViewById(R.id.listView1);
        arrayList = new ArrayList<>();
        adapter = new NotesAdapter(this, R.layout.row_notes, arrayList);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //bắt sự kiện cho menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuAddNotes){
            DialogThem();
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogThem() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_notes);

        // Căn chỉnh dialog_add_notes vừa với khung hình
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        // Ánh xạ view
        EditText editText = dialog.findViewById(R.id.editTextName);
        Button buttonAdd = dialog.findViewById(R.id.buttonThem);
        Button buttonHuy = dialog.findViewById(R.id.buttonHuy);

        // Bắt sự kiện nút Thêm
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên Notes", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseHandler.QueryData("INSERT INTO Notes VALUES(null, '" + name + "')");
                    Toast.makeText(MainActivity.this, "Đã thêm Notes", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    databaseSQLite(); // Gọi hàm load lại dữ liệu
                }
            }
        });

        // Bắt sự kiện nút Hủy
        buttonHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void DialogCapNhatNotes(String name, int id) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_notes);

        // Căn chỉnh dialog_edit_notes vừa với khung hình
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Ánh xạ View
        EditText editText = dialog.findViewById(R.id.editTextName);
        Button buttonEdit = dialog.findViewById(R.id.buttonEdit);
        Button buttonHuy = dialog.findViewById(R.id.buttonHuy);

        // Gán giá trị ban đầu cho EditText
        editText.setText(name);

        // Bắt sự kiện nút "Cập Nhật"
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editText.getText().toString().trim();

                if (!newName.isEmpty()) {
                    databaseHandler.QueryData("UPDATE Notes SET NameNotes ='" + newName + "' WHERE Id = '" + id + "'");
                    Toast.makeText(MainActivity.this, "Đã cập nhật Notes thành công", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    databaseSQLite(); // Gọi hàm load lại dữ liệu
                }
                else {
                    Toast.makeText(MainActivity.this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void DialogDelete(String name, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn xóa Notes \"" + name + "\" này không?");

        // Nút xác nhận xóa
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHandler.QueryData("DELETE FROM Notes WHERE Id = " + id);

                Toast.makeText(MainActivity.this, "Đã xóa Notes \"" + name + "\" thành công", Toast.LENGTH_SHORT).show();

                databaseSQLite();
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

}