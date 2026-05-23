package com.snack.controller;

import com.snack.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Tag(name = "文件管理", description = "图片上传与访问")
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final Path uploadRoot;
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");
    private static final long MAX_SIZE = 2 * 1024 * 1024;  // 2MB

    public FileController(@Value("${app.upload.base-path:./uploads}") String basePath) {
        this.uploadRoot = Paths.get(basePath).toAbsolutePath().normalize();
        new File(uploadRoot.resolve("snacks").toString()).mkdirs();
        new File(uploadRoot.resolve("avatars").toString()).mkdirs();
    }

    @Operation(summary = "上传零食图片")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        validateFile(file, MAX_SIZE);
        String filename = saveFile(file, "snacks");
        Map<String, String> data = new HashMap<>();
        data.put("url", "/api/files/snacks/" + filename);
        return Result.success(data);
    }

    @Operation(summary = "访问零食图片")
    @GetMapping("/snacks/{filename}")
    public Resource getSnackImage(@PathVariable String filename) {
        return loadResource("snacks", filename);
    }

    @Operation(summary = "访问头像图片")
    @GetMapping("/avatars/{filename}")
    public Resource getAvatarImage(@PathVariable String filename) {
        return loadResource("avatars", filename);
    }

    // ---------- package-private helpers (used by AuthController) ----------

    String saveAvatar(MultipartFile file) {
        validateFile(file, 1 * 1024 * 1024);  // avatar max 1MB
        return "/api/files/avatars/" + saveFile(file, "avatars");
    }

    void deleteIfLocal(String url) {
        if (url != null && url.startsWith("/api/files/")) {
            try {
                Path target = uploadRoot.resolve(url.substring("/api/files/".length())).normalize();
                if (target.startsWith(uploadRoot)) {
                    target.toFile().delete();
                }
            } catch (Exception ignored) {}
        }
    }

    // ---------- private ----------

    private void validateFile(MultipartFile file, long maxSize) {
        if (file.isEmpty()) throw new RuntimeException(new Exception("文件为空"));
        String orig = file.getOriginalFilename();
        if (orig == null) throw new RuntimeException(new Exception("文件名无效"));
        String ext = orig.substring(orig.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) throw new RuntimeException(new Exception("仅支持 jpg/jpeg/png/webp"));
        if (file.getSize() > maxSize) throw new RuntimeException(new Exception("文件大小超限"));
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            throw new RuntimeException(new Exception("非图片类型"));
    }

    private String saveFile(MultipartFile file, String subDir) {
        try {
            String orig = file.getOriginalFilename();
            String ext = orig.substring(orig.lastIndexOf('.')).toLowerCase();
            String filename = UUID.randomUUID().toString() + ext;
            File dest = uploadRoot.resolve(subDir).resolve(filename).toFile();
            file.transferTo(dest);
            return filename;
        } catch (Exception e) {
            throw new RuntimeException(new Exception("文件保存失败"));
        }
    }

    private Resource loadResource(String subDir, String filename) {
        try {
            // 安全检查
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\"))
                throw new RuntimeException(new Exception("非法文件名"));
            Path file = uploadRoot.resolve(subDir).resolve(filename).normalize();
            if (!file.startsWith(uploadRoot))
                throw new RuntimeException(new Exception("非法路径"));
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) return resource;
            throw new RuntimeException(new Exception("文件不存在"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(new Exception("文件读取失败"));
        }
    }
}
