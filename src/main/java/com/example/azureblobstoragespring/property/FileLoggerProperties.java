package com.example.azureblobstoragespring.property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "files")
@AllArgsConstructor
@NoArgsConstructor
public class FileLoggerProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_download_url")
    private String fileDownloadUri;

    @Column(name = "upload_date")
    private String uploadDate;

    @Column(name = "file_type")
    private String fileType;

}
