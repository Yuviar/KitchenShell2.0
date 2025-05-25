
package print.model;

public class FieldStruk {

    /**
     * @return the nama_menu
     */
    public String getNama_menu() {
        return nama_menu;
    }

    /**
     * @param nama_menu the nama_menu to set
     */
    public void setNama_menu(String nama_menu) {
        this.nama_menu = nama_menu;
    }

    /**
     * @return the jumlah
     */
    public int getJumlah() {
        return jumlah;
    }

    /**
     * @param jumlah the jumlah to set
     */
    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    /**
     * @return the harga
     */
    public double getHarga() {
        return harga;
    }

    /**
     * @param harga the harga to set
     */
    public void setHarga(double harga) {
        this.harga = harga;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    public FieldStruk() {
    }

    public FieldStruk(String nama_menu, int jumlah, double harga, double total) {
        this.nama_menu = nama_menu;
        this.jumlah = jumlah;
        this.harga = harga;
        this.total = total;
    }
    
    private String nama_menu;
    private int jumlah;
    private double harga;
    private double total;
}
