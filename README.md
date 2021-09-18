# cadastro-em-massa-com-spring-batch
Exemplo de uso de aplicação utiliando Spring Batch para cadastro em massa de usuários salvos  em arquivos CSV em um diretório pendente. 
Primeiro é realiado a validação dos campos dos registros, é validado se o usuário já está cadastrado numa base em memória (H2) e valida os campos utiliando Bean Validation, após a válidação os arquivos validados são movidos para um diretório validados e com inconsistência vão para uma diretório invalidados.
Em seguida, os arquivos validados são lidos e é realiada a insersão dos novos usuários em banco de dados.

O Processo de execução de processamento é realiado a cada 10segundos utiliando o @Scheduled

## O que é Spring Batch?
Conforme a descrição da [documentação do Spring Batch](https://docs.spring.io/spring-batch/docs/current/reference/html/spring-batch-intro.html#spring-batch-intro) é uma estrutura de lote leve e abrangente projetada para permitir o desenvolvimento de aplicativos de lote robustos, vitais para as operações diárias de sistemas corporativos. O Spring Batch se baseia nas características do Spring Framework que as pessoas esperam (produtividade, abordagem de desenvolvimento baseada em POJO e facilidade geral de uso), ao mesmo tempo que torna mais fácil para os desenvolvedores acessar e aproveitar mais serviços corporativos avançados quando necessário. Spring Batch não é uma estrutura de agendamento. Existem muitos planejadores corporativos bons (como Quartz, Tivoli, Control-M, etc.) disponíveis nos espaços comerciais e de software livre. Destina-se a funcionar em conjunto com um planejador, não substituir um planejador.


## Pré requisitos

- Java 11
- Apache Maven

