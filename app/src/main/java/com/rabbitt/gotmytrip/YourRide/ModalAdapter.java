package com.rabbitt.gotmytrip.YourRide;

public class ModalAdapter {
//     [book_id] => 3
//            [prefix] => OST
//            [starting_point] => usha villa b-block mg road near 32 mystone club, Shivaji Nagar, Bengaluru, Karnataka 560001, India,null
//            [destination_point] => 17, Benson Cross Rd, Byadarahalli, Benson Town, Bengaluru, Karnataka 560046, India,null
//            [dateon] => 2020-01-30
//            [status] => 0
//            [v_type] => Prime
    String book_id, prefix, start, end, date, status, v_type;

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getV_type() {
        return v_type;
    }

    public void setV_type(String v_type) {
        this.v_type = v_type;
    }
}
