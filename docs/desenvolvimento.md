# Desenvolvimento e validação

## Comandos

Execute sempre uma tarefa Gradle por vez para evitar concorrência entre os
caches dos loaders:

```powershell
.\gradlew.bat buildAll
.\gradlew.bat :forge1201:build
.\gradlew.bat :neoforge:build
.\gradlew.bat runForgeClient
.\gradlew.bat runNeoForgeClient
.\gradlew.bat runForgeServer
.\gradlew.bat runNeoForgeServer
.\gradlew.bat verifyAll
.\gradlew.bat -PgaugeTests runForgeGameTests runNeoForgeGameTests
```

Os diretórios de execução e resultados são separados em `forge1201/runs` e
`neoforge/runs`. Os JARs e sources ficam em `build/libs` de cada módulo.

## Evidências e pendências

| Camada | Evidência atual |
|---|---|
| Dependências Create | JAR local Create 1.20.1 6.0.8 inspecionado; NeoForge usa Create 6.0.10 configurado no Gradle. |
| Build Forge e NeoForge | `buildAll` aprovado em 16/10/2026 após as correções de identidade dos gauges. |
| Boot cliente/servidor | Servidores GameTest aprovados e clientes dos dois loaders abertos no mundo existente nesta revisão. |
| Receitas maiores e automação completa por pacotes | Pendente. |
| Persistência, wrench e drops | 12 GameTests por loader aprovados; [cobertura detalhada](identidade-dos-gauges.md). Multiplayer real pendente. |
| Revisão manual por Computer Use | Forge: colocação conjunta, pickblock, wrench em Survival e persistência aprovados. NeoForge: sintonização e colocação conjunta aprovadas; sessão interrompida a pedido do usuário. |

Uma verificação é considerada aprovada somente depois de produzir relatório novo
para o loader correspondente. Inicialização e compilação não substituem a
validação manual do fluxo de criação.
