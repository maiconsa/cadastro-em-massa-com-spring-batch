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
- Configurar o properties location.base com o caminho onde estará a pasta "pendentes" que conterá os arquivos a serem processados.

## Como executar?
Para gerar o pacote .jar execute o comando:

```bsh
  mvn clean package
```
Para executar o .jar da aplicação execute :
```bsh
  javar -jar [CAMINHO-ATE-PROJETO]/target/jobs-com-spring-batch-0.0.1-SNAPSHOT.jar
```

## Evidências de funcionamento

Para o exemplo foi utiliado o CAMINHO-BASE como:
    /home/maiconsa/Documentos/batchFiles/

Após rodar o programa com o passo anterior. Foi inserido o [arquivo de exemplo arquivo01.csv](https://github.com/maiconsa/cadastro-em-massa-com-spring-batch/blob/main/examples/arquivo01.csv) no caminho [CAMINHO-BASE]/pendentes. Então, após o JOB ser executado o arquivo foi validado e movido para [location.base]/validados, conforme imagem abaixo.

![Imagem arquivo validado](https://github.com/maiconsa/cadastro-em-massa-com-spring-batch/blob/main/imagens/arquivo-validado.png)

Com os arquivos válidados o JOB executou a segunda etapa de cadastro de usuários, conforme imagem ho H2.
![Usuários cadastrado no H2](https://github.com/maiconsa/cadastro-em-massa-com-spring-batch/blob/main/imagens/usuarios-cadastrados-h2.png)

Por fim, para validar campos inconsistente desta ve foi inserido o arquivo [arquivo de exemplo arquivo02.csv](https://github.com/maiconsa/cadastro-em-massa-com-spring-batch/blob/main/examples/arquivo02.csv) no diretório pendente. Após execução automática do JOB o arquivo foi validado e como continha campos inválido foi movido para o diretório [CAMINHO-BASE]/invalidos, visto imagem abaixo.

![Arquivo invalido](https://github.com/maiconsa/cadastro-em-massa-com-spring-batch/blob/main/imagens/arquivo-invalido.png)

Para informa o usuários sobre qual campo foi inválidado foi inserido uma coluna contendo as informações das validações realiadas no registros. segue imagem com o arquivo inválido.

![Registro inválidos](https://github.com/maiconsa/cadastro-em-massa-com-spring-batch/blob/main/imagens/registro-invalidos%5D.png)


