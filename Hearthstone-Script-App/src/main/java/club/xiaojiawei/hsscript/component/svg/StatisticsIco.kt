package club.xiaojiawei.hsscript.component.svg

import club.xiaojiawei.controls.ico.AbstractIco
import club.xiaojiawei.controls.images.ImagesLoader
import org.girod.javafx.svgimage.SVGLoader

/**
 * @author 肖嘉威
 * @date 2025/3/13 23:09
 */
class StatisticsIco : AbstractIco {

    constructor() : this(null)

    constructor(color: String?) : super(color) {
        val svgImage = SVGLoader.load(ImagesLoader::class.java.getResource("/fxml/component/svg/${this::class.java.simpleName}.svg"))
        if (svgImage != null) {
            this.maxWidth = svgImage.width
            children.add(svgImage)
        }
    }

}