package ru.nsu.dashkovskii.dsl

import groovy.util.DelegatingScript
import org.codehaus.groovy.control.CompilerConfiguration
import ru.nsu.dashkovskii.model.CheckerConfig

/**
 * Загрузчик конфигурации из Groovy-скрипта.
 */
class ConfigLoader {

    /** Имя скрипта по умолчанию (аналог build.gradle). */
    static final String DEFAULT_SCRIPT = 'checker.groovy'

    /** Ищет DEFAULT_SCRIPT в указанной директории. */
    CheckerConfig loadFromDir(File dir) {
        File script = new File(dir, DEFAULT_SCRIPT)
        if (!script.exists()) {
            throw new IllegalStateException(
                    "Не найден скрипт ${DEFAULT_SCRIPT} в ${dir.absolutePath}")
        }
        return load(script)
    }

    /** Загружает конфигурацию из указанного файла. */
    CheckerConfig load(File scriptFile) {
        def cc = new CompilerConfiguration()
        cc.scriptBaseClass = DelegatingScript.name
        def shell = new GroovyShell(ConfigLoader.class.classLoader, new Binding(), cc)
        def script = (DelegatingScript) shell.parse(scriptFile)
        def builder = new ConfigBuilder(scriptDir: scriptFile.parentFile)
        script.setDelegate(builder)
        script.run()
        return builder.config
    }
}
