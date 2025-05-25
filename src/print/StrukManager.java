package print;

import config.DatabaseConfig;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import print.model.ParameterStruk;

public class StrukManager {

    private static StrukManager instance;
    Connection con = null;

    private JasperReport strukBayar;

    public static StrukManager getIntance() {
        if (instance == null) {
            instance = new StrukManager();
        }
        return instance;
    }

    private StrukManager() {

    }

    public void compileStruk() throws JRException {
        strukBayar = JasperCompileManager.compileReport(getClass().getResourceAsStream("/print/struk.jrxml"));
    }

    public void printStruk(ParameterStruk data) throws JRException {
        Map para = new HashMap();
        para.put("nama_kasir", data.getNama_kasir());
        para.put("tanggal", data.getTanggal());
        para.put("kode_transaksi", data.getKode_transaksi());
        para.put("nama_pelanggan", data.getNama_pelanggan());
        para.put("total", data.getTotal());
        para.put("member", data.getMember());
        para.put("diskon", data.getDiskon());
        para.put("bayar", data.getBayar());
        para.put("kembalian", data.getKembalian());
        para.put("point", data.getPoint());
        con = DatabaseConfig.getConnection();
        JasperPrint print = JasperFillManager.fillReport(strukBayar, para, con);
        
        view(print);
    }

    private void view(JasperPrint print) throws JRException {
        JasperViewer.viewReport(print, false);
    }
}
