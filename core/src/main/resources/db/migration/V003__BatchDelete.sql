CREATE OR REPLACE FUNCTION delete_by_export_id(export_id_to_delete bigint)
RETURNS bigint AS $$
DECLARE
    rows_deleted bigint;
BEGIN
    RAISE NOTICE 'Lösche Daten für export_id %...', export_id_to_delete;
    
    RAISE NOTICE 'Lösche BEWERBUNG...';
    DELETE FROM BEWERBUNG k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche VERTRAG...';
    DELETE FROM VERTRAG k WHERE k.export_id = export_id_to_delete;
    
    RAISE NOTICE 'Lösche KINDDATEN...';
    DELETE FROM KINDDATEN k WHERE k.export_id = export_id_to_delete;
    
    RAISE NOTICE 'Lösche BESONDERE_LAGE...';
    DELETE FROM BESONDERE_LAGE k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche ELTERNPRIORITAETSGRUND...';
    DELETE FROM ELTERNPRIORITAETSGRUND k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche ALTERSGRUPPE...';
    DELETE FROM ALTERSGRUPPE k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche BRING_ABHOLZEIT...';
    DELETE FROM BRING_ABHOLZEIT k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche GRUPPE...';
    DELETE FROM GRUPPE k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche INTEGRATION...';
    DELETE FROM INTEGRATION k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche KONTINGENT...';
    DELETE FROM KONTINGENT k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche VERTRAGSABSCHNITT...';
    DELETE FROM VERTRAGSABSCHNITT k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche EXPORT_ERROR...';
    DELETE FROM EXPORT_ERROR k WHERE k.export_id = export_id_to_delete;
    RAISE NOTICE 'Lösche SORGEBERECHTIGTER...';
    DELETE FROM SORGEBERECHTIGTER k WHERE k.export_id = export_id_to_delete;
    
    RAISE NOTICE 'Lösche KIND...';
    DELETE FROM KIND k WHERE k.export_id = export_id_to_delete;
    GET DIAGNOSTICS rows_deleted = ROW_COUNT;
    
    UPDATE EXPORT_RUN SET STATUS = 'DELETED' WHERE ID = export_id_to_delete;
    RAISE NOTICE 'Löschen abgeschlossen.';
    RETURN rows_deleted;
EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Error occurred: %', SQLERRM;
END;
$$ LANGUAGE plpgsql;