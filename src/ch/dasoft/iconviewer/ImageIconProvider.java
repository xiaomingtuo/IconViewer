package ch.dasoft.iconviewer;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.twelvemonkeys.util.LRUHashMap;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;


/**
 * Created by David Sommer on 19.05.17.
 *
 * @author davidsommer
 */
public class ImageIconProvider extends IconProvider {

    private static final int IMG_WIDTH = 16;
    private static final int IMG_HEIGHT = 16;
    private HashMap<String, Image> cacheImage = new LRUHashMap<>();
    private HashMap<Image, ImageIcon> cacheIcon = new LRUHashMap<>();

    public Icon getIcon(@NotNull PsiElement psiElement, int flags) {
        PsiFile containingFile = psiElement.getContainingFile();
        if (checkImagePath(containingFile)) {
            Image image;
            try {
//                image = ImageLoader.loadFromStream(new BufferedInputStream(new FileInputStream(containingFile.getVirtualFile().getCanonicalFile().getCanonicalPath())));
                String path = containingFile.getVirtualFile().getCanonicalFile().getCanonicalPath();
                if (cacheImage.get(path) != null) {
                    image = cacheImage.get(path);
                } else {
                    image = Toolkit.getDefaultToolkit().getImage(path);
                    cacheImage.put(path, image);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("Error loading preview Icon - " + containingFile.getVirtualFile().getCanonicalFile().getCanonicalPath());
            }

            if (image != null) {
                ImageIcon icon;
                if (cacheIcon.get(image) != null) {
                    icon = cacheIcon.get(image);
                } else {
                    icon = new ImageIcon(image.getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH));
                    cacheIcon.put(image, icon);
                }
                return icon;
            }
        }
        return null;
    }

    private boolean checkImagePath(PsiFile containingFile) {
        return containingFile != null && containingFile.getVirtualFile() != null && containingFile.getVirtualFile().getCanonicalFile() != null && containingFile.getVirtualFile().getCanonicalFile().getCanonicalPath() != null && UIUtils.isImageFile(containingFile.getName()) && !containingFile.getVirtualFile().getCanonicalFile().getCanonicalPath().contains(".jar");
    }
}