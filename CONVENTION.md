===================================================================
HƯỚNG DẪN & SYSTEM PROMPT CHO CODING AGENT (ANDROID JAVA LIVE-CODING)
===================================================================

[PHẦN 1] SYSTEM PROMPT DÁN VÀO AI (Cursor / Claude Code / Copilot / Gemini)
-------------------------------------------------------------------
Hãy đóng vai một Lập trình viên Android Senior. Tạo mã nguồn Java Android Native tuân thủ CHẮC CHẮN các quy tắc sau để sinh viên dễ thuộc lòng và live-code:
1. KHÔNG sử dụng Dependency Injection (Hilt, Dagger), KHÔNG dùng Room ORM, KHÔNG dùng RxJava/LiveData/ViewModel phức tạp, KHÔNG dùng ViewBinding/DataBinding.
2. CHỈ sử dụng findViewById(), SQLiteOpenHelper truyền thống, và Thread + runOnUiThread() cho xử lý bất đồng bộ.
3. Cấu trúc code theo mô hình MVC đơn giản nhất. Đặt tên biến tường minh, ngắn gọn, dễ nhớ.
4. Mọi hàm logic phải chia nhỏ (< 20 dòng), không lồng callback quá 2 cấp.
5. Dùng Anonymous Class truyền thống (new Runnable(), new View.OnClickListener())Thay vì Lambda Expression để người mới đọc dễ hiểu luồng.
6. Viết comment bằng tiếng Việt ngắn gọn giải thích tác dụng của từng khối code quan trọng.


===================================================================
[PHẦN 2] 5 CODING PATTERNS CHUẨN ĐỂ HỌC THUỘC & ĐỐI PHÓ LIVE-CODING
===================================================================

--- PATTERN 1: RECYCLERVIEW ADAPTER THUẦN (15 DÒNG CƠ BẢN) ---
// Mục tiêu: Bị xóa Adapter là gõ lại ngay trong 2-3 phút

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<String> list;

    public HistoryAdapter(List<String> list) { 
        this.list = list; 
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvText.setText(list.get(position));
    }

    @Override 
    public int getItemCount() { 
        return list == null ? 0 : list.size(); 
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_item_text);
        }
    }
}


--- PATTERN 2: SQLITE OPEN HELPER CƠ BẢN (CRUD TRUYỀN THỐNG) ---
// Mục tiêu: Tránh dùng Room ORM phức tạp, dùng SQLiteDatabase thuần dễ thuộc

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "game.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) { 
        super(context, DB_NAME, null, DB_VERSION); 
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT, score INTEGER)";
        db.execSQL(createTable);
    }

    @Override 
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS history");
        onCreate(db);
    }

    // Hàm Thêm
    public void insertScore(int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("score", score);
        db.insert("history", null, cv);
        db.close();
    }

    // Hàm Đọc
    public List<Integer> getAllScores() {
        List<Integer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT score FROM history", null);
        if (c.moveToFirst()) {
            do { 
                list.add(c.getInt(0)); 
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
}


--- PATTERN 3: XỬ LÝ LUỒNG / BẤT ĐỒNG BỘ ĐƠN GIẢN (THREAD + RUNONUITHREAD) ---
// Mục tiêu: Không bao giờ bị crash 'NetworkOnMainThreadException', dễ giải thích luồng

private void doBackgroundTask() {
    new Thread(new Runnable() {
        @Override
        public void run() {
            // 1. Code chạy ngầm ở đây (Xử lý tính toán, Socket, Network, DB)
            final String result = "Kết quả từ ngầm";

            // 2. Trả kết quả về giao diện (UI Thread)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(result);
                }
            });
        }
    }).start();
}


--- PATTERN 4: SINGLETON MANAGER (QUẢN LÝ DÙNG CHUNG) ---
// Áp dụng cho: SoundManager, GameLogic, FirebaseHelper

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer mediaPlayer;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playSound(Context context, int soundResId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, soundResId);
        mediaPlayer.start();
    }
}


--- PATTERN 5: GAME COUNTDOWN TIMER (HẸN GIỜ ĐỒNG BỘ UI) ---
// Mục tiêu: Bộ đếm thời gian cho ván đấu, hiển thị UI mượt mà

private CountDownTimer timer;

private void startTimer(long totalMillis) {
    if (timer != null) timer.cancel();

    timer = new CountDownTimer(totalMillis, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long secondsLeft = millisUntilFinished / 1000;
            tvTimer.setText("Thời gian: " + secondsLeft + "s");
        }

        @Override
        public void onFinish() {
            tvTimer.setText("Hết giờ!");
            onGameOver();
        }
    }.start();
}