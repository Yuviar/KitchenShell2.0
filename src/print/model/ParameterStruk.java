
package print.model;

public class ParameterStruk {

    /**
     * @return the nama_kasir
     */
    public String getNama_kasir() {
        return nama_kasir;
    }

    /**
     * @param nama_kasir the nama_kasir to set
     */
    public void setNama_kasir(String nama_kasir) {
        this.nama_kasir = nama_kasir;
    }

    /**
     * @return the tanggal
     */
    public String getTanggal() {
        return tanggal;
    }

    /**
     * @param tanggal the tanggal to set
     */
    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    /**
     * @return the kode_transaksi
     */
    public String getKode_transaksi() {
        return kode_transaksi;
    }

    /**
     * @param kode_transaksi the kode_transaksi to set
     */
    public void setKode_transaksi(String kode_transaksi) {
        this.kode_transaksi = kode_transaksi;
    }

    /**
     * @return the nama_pelanggan
     */
    public String getNama_pelanggan() {
        return nama_pelanggan;
    }

    /**
     * @param nama_pelanggan the nama_pelanggan to set
     */
    public void setNama_pelanggan(String nama_pelanggan) {
        this.nama_pelanggan = nama_pelanggan;
    }

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     * @return the member
     */
    public String getMember() {
        return member;
    }

    /**
     * @param member the member to set
     */
    public void setMember(String member) {
        this.member = member;
    }

    /**
     * @return the diskon
     */
    public String getDiskon() {
        return diskon;
    }

    /**
     * @param diskon the diskon to set
     */
    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    /**
     * @return the bayar
     */
    public String getBayar() {
        return bayar;
    }

    /**
     * @param bayar the bayar to set
     */
    public void setBayar(String bayar) {
        this.bayar = bayar;
    }

    /**
     * @return the kembalian
     */
    public String getKembalian() {
        return kembalian;
    }

    /**
     * @param kembalian the kembalian to set
     */
    public void setKembalian(String kembalian) {
        this.kembalian = kembalian;
    }

    /**
     * @return the point
     */
    public String getPoint() {
        return point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(String point) {
        this.point = point;
    }

  

    public ParameterStruk() {
    }

    public ParameterStruk(String nama_kasir, String tanggal, String kode_transaksi, String nama_pelanggan, String total, String member, String diskon, String bayar, String kembalian, String point) {
        this.nama_kasir = nama_kasir;
        this.tanggal = tanggal;
        this.kode_transaksi = kode_transaksi;
        this.nama_pelanggan = nama_pelanggan;
        this.total = total;
        this.member = member;
        this.diskon = diskon;
        this.bayar = bayar;
        this.kembalian = kembalian;
        this.point = point;
    }

    private String nama_kasir;
    private String tanggal;
    private String kode_transaksi;
    private String nama_pelanggan;
    private String total;
    private String member;
    private String diskon;
    private String bayar;
    private String kembalian;
    private String point;
}
