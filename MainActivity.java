package com.gymsaathi.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(9, 12, 11);
    private final int PANEL = Color.rgb(20, 25, 23);
    private final int GREEN = Color.rgb(183, 255, 69);
    private final int MUTED = Color.rgb(155, 167, 160);
    private final int RED = Color.rgb(255, 132, 132);
    private LinearLayout root, list;
    private TextView total, pending, collected, empty;
    private EditText search;
    private SharedPreferences prefs;
    private JSONArray members;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);
        prefs = getSharedPreferences("gymsaathi", MODE_PRIVATE);
        try { members = new JSONArray(prefs.getString("members", "[]")); }
        catch (Exception e) { members = new JSONArray(); }
        build();
        render();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius);
        d.setStroke(1, Color.rgb(43, 55, 49));
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private void build() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        root = column(); root.setPadding(18, 20, 18, 30);
        scroll.addView(root);
        setContentView(scroll);

        LinearLayout brand = new LinearLayout(this);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        TextView logo = text("G", 28, BG, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(shape(GREEN, 22));
        brand.addView(logo, new LinearLayout.LayoutParams(56, 56));
        LinearLayout brandText = column(); brandText.setPadding(12, 0, 0, 0);
        brandText.addView(text("GymSaathi", 25, Color.WHITE, true));
        brandText.addView(text("YOUR GYM. YOUR PARTNER.", 10, MUTED, true));
        brand.addView(brandText);
        root.addView(brand);

        TextView eyebrow = text("GYM OWNER DASHBOARD", 12, GREEN, true);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(-1, -2);
        ep.topMargin = 25; root.addView(eyebrow, ep);
        TextView headline = text("Run your gym.\nNot your paperwork.", 27, Color.WHITE, true);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = 7; hp.bottomMargin = 17; root.addView(headline, hp);

        LinearLayout stats = new LinearLayout(this);
        total = statView("0", "TOTAL MEMBERS");
        pending = statView("₹0", "FEES PENDING");
        LinearLayout.LayoutParams s1 = new LinearLayout.LayoutParams(0, -2, 1);
        s1.setMargins(0, 0, 5, 0); stats.addView(total, s1);
        LinearLayout.LayoutParams s2 = new LinearLayout.LayoutParams(0, -2, 1);
        s2.setMargins(5, 0, 0, 0); stats.addView(pending, s2);
        root.addView(stats);

        collected = text("₹0 collected in fees", 13, GREEN, true);
        collected.setPadding(4, 12, 4, 2); root.addView(collected);

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        Button report = button("Reports");
        report.setOnClickListener(v -> showReports());
        Button add = button("+ Add member");
        add.setOnClickListener(v -> memberDialog(-1));
        LinearLayout.LayoutParams a1 = new LinearLayout.LayoutParams(0, -2, 1);
        a1.setMargins(0, 0, 6, 0); actions.addView(report, a1);
        LinearLayout.LayoutParams a2 = new LinearLayout.LayoutParams(0, -2, 1);
        a2.setMargins(6, 0, 0, 0); actions.addView(add, a2);
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1, -2);
        ap.topMargin = 19; root.addView(actions, ap);

        TextView membersTitle = text("Members", 20, Color.WHITE, true);
        LinearLayout.LayoutParams mt = new LinearLayout.LayoutParams(-1, -2);
        mt.topMargin = 23; mt.bottomMargin = 10; root.addView(membersTitle, mt);

        search = field("Search name or phone", InputType.TYPE_CLASS_TEXT);
        root.addView(search);
        search.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { render(); }
            public void afterTextChanged(android.text.Editable e) {}
        });
        list = column();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = 12; root.addView(list, lp);
        root.addView(text("LOCAL MODE • DATA SAVED ON THIS PHONE", 10, MUTED, false));
    }

    private TextView statView(String value, String label) {
        TextView t = text(value + "\n" + label, 18, GREEN, true);
        t.setPadding(14, 17, 8, 17);
        t.setBackground(shape(PANEL, 18));
        return t;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label); b.setTextColor(BG); b.setTextSize(13);
        b.setAllCaps(false); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setPadding(8, 4, 8, 4); b.setBackground(shape(GREEN, 16));
        return b;
    }

    private Button secondaryButton(String label) {
        Button b = button(label);
        b.setTextColor(Color.WHITE);
        b.setBackground(shape(Color.rgb(42, 51, 47), 15));
        return b;
    }

    private EditText field(String hint, int type) {
        EditText e = new EditText(this);
        e.setSingleLine(true); e.setHint(hint); e.setTextColor(Color.WHITE);
        e.setHintTextColor(MUTED); e.setTextSize(14); e.setInputType(type);
        e.setPadding(13, 10, 13, 10); e.setBackground(shape(Color.rgb(15, 19, 17), 14));
        return e;
    }

    private void memberDialog(int editIndex) {
        final JSONObject old = editIndex >= 0 ? members.optJSONObject(editIndex) : null;
        LinearLayout form = column(); form.setPadding(4, 8, 4, 2);
        EditText name = field("Member name *", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText phone = field("Phone (optional)", InputType.TYPE_CLASS_PHONE);
        EditText plan = field("Plan (e.g. Monthly)", InputType.TYPE_CLASS_TEXT);
        EditText fee = field("Fee amount (₹)", InputType.TYPE_CLASS_NUMBER);
        EditText expiry = field("Expiry date (YYYY-MM-DD)", InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        plan.setText("Monthly");
        if (old != null) {
            name.setText(old.optString("name"));
            phone.setText(old.optString("phone"));
            plan.setText(old.optString("plan", "Monthly"));
            fee.setText(String.valueOf(old.optInt("fee")));
            expiry.setText(old.optString("expiry"));
        }
        addFormField(form, name); addFormField(form, phone); addFormField(form, plan);
        addFormField(form, fee); addFormField(form, expiry);
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(old == null ? "Add gym member" : "Edit gym member")
            .setView(form).setNegativeButton("Cancel", null)
            .setPositiveButton(old == null ? "Save member" : "Save changes", null).create();
        dialog.setOnShowListener(x -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            if (n.isEmpty()) { name.setError("Name is required"); return; }
            int feeAmount;
            try { feeAmount = fee.getText().toString().trim().isEmpty() ? 0 : Integer.parseInt(fee.getText().toString().trim()); }
            catch (Exception ex) { fee.setError("Enter a valid amount"); return; }
            try {
                JSONObject m = old == null ? new JSONObject() : old;
                if (old == null) { m.put("id", System.currentTimeMillis()); m.put("paid", false); }
                m.put("name", n);
                m.put("phone", phone.getText().toString().trim());
                m.put("plan", plan.getText().toString().trim().isEmpty() ? "Monthly" : plan.getText().toString().trim());
                m.put("fee", Math.max(0, feeAmount));
                m.put("expiry", expiry.getText().toString().trim());
                if (old == null) members.put(m);
                save(); render(); dialog.dismiss();
            } catch (Exception ignored) {}
        }));
        dialog.show();
    }

    private void addFormField(LinearLayout form, View view) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = 8; form.addView(view, p);
    }

    private void save() { prefs.edit().putString("members", members.toString()).apply(); }

    private void showReports() {
        int due = 0, paid = 0, totalFees = 0, paidCount = 0;
        for (int i=0; i<members.length(); i++) {
            JSONObject m = members.optJSONObject(i);
            if (m == null) continue;
            int fee = Math.max(0, m.optInt("fee"));
            totalFees += fee;
            if (m.optBoolean("paid")) { paid += fee; paidCount++; }
            else due += fee;
        }
        String report = "Members: " + members.length()
            + "\n\nFees recorded: ₹" + totalFees
            + "\nFees collected: ₹" + paid
            + "\nFees pending: ₹" + due
            + "\nPaid members: " + paidCount
            + "\nPending members: " + (members.length() - paidCount)
            + "\n\nNote: totals include the full fee entered for each member. This is a simple local report, not an accounting statement.";
        new AlertDialog.Builder(this).setTitle("GymSaathi reports")
            .setMessage(report).setPositiveButton("Done", null).show();
    }

    private void render() {
        if (total == null || pending == null || list == null) return;
        int count = members.length(), due = 0, paid = 0;
        for (int i=0; i<members.length(); i++) {
            JSONObject m = members.optJSONObject(i);
            if (m != null) {
                if (m.optBoolean("paid")) paid += Math.max(0, m.optInt("fee"));
                else due += Math.max(0, m.optInt("fee"));
            }
        }
        total.setText(count + "\nTOTAL MEMBERS");
        pending.setText("₹" + due + "\nFEES PENDING");
        collected.setText("₹" + paid + " collected in fees");
        list.removeAllViews();
        String query = search == null ? "" : search.getText().toString().trim().toLowerCase();
        int shown = 0;
        for (int i=count-1; i>=0; i--) {
            final int index = i;
            JSONObject m = members.optJSONObject(i); if (m == null) continue;
            String n = m.optString("name");
            String p = m.optString("phone");
            if (!query.isEmpty() && !n.toLowerCase().contains(query) && !p.toLowerCase().contains(query)) continue;
            shown++;
            LinearLayout card = column(); card.setPadding(14, 13, 14, 13); card.setBackground(shape(PANEL, 17));
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2); cp.bottomMargin = 10; list.addView(card, cp);
            TextView nm = text(n, 17, Color.WHITE, true); card.addView(nm);
            String meta = (p.isEmpty() ? "No phone" : p) + "  •  " + m.optString("plan", "Monthly");
            card.addView(text(meta, 12, MUTED, false));
            String exp = m.optString("expiry", "");
            if (!exp.isEmpty()) card.addView(text("Expiry: " + exp, 12, MUTED, false));
            card.addView(text("Fee: ₹" + m.optInt("fee") + "  •  " + (m.optBoolean("paid") ? "PAID" : "PENDING"),
                13, m.optBoolean("paid") ? GREEN : RED, true));
            LinearLayout actions = new LinearLayout(this); actions.setGravity(Gravity.END);
            Button paidBtn = secondaryButton(m.optBoolean("paid") ? "Mark pending" : "Mark paid");
            Button edit = secondaryButton("Edit");
            Button del = secondaryButton("Delete");
            del.setTextColor(RED);
            LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, -2, 1);
            bp.setMargins(3, 8, 3, 0);
            actions.addView(paidBtn, bp); actions.addView(edit, bp); actions.addView(del, bp);
            card.addView(actions);
            paidBtn.setOnClickListener(v -> {
                JSONObject obj = members.optJSONObject(index);
                if (obj != null) { try { obj.put("paid", !obj.optBoolean("paid")); } catch (Exception ignored) {} save(); render(); }
            });
            edit.setOnClickListener(v -> memberDialog(index));
            del.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage("Delete " + n + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d,w) -> {
                    JSONArray next = new JSONArray();
                    for (int j=0; j<members.length(); j++) if (j != index) next.put(members.optJSONObject(j));
                    members = next; save(); render();
                }).show());
        }
        if (shown == 0) {
            TextView no = text(query.isEmpty() ? "No members yet. Tap “+ Add member” to get started." : "No members match that search.", 14, MUTED, false);
            no.setPadding(18, 24, 18, 24); no.setGravity(Gravity.CENTER); no.setBackground(shape(PANEL, 17));
            list.addView(no);
        }
    }
}
