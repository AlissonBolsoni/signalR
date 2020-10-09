package br.com.alissonbolsoni.signalr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import java.lang.Exception
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var connection: HubConnection
    private var disconnected = true

    companion object{
        private val URL_BASE = "http://172.107.85.164:8094/"
        private val HUB = "tabletHub"
        private val GET_TELEMETRY = "RecebaTelemetria"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createConnection("AAA-1111")
        startTelemetry()
    }

    private fun startTelemetry() {
        if (this::connection.isInitialized){
            if (connection.connectionState == HubConnectionState.CONNECTED){
                //TODO
            }

            if (connection.connectionState == HubConnectionState.DISCONNECTED){
                //TODO
            }

            connection.on(
                GET_TELEMETRY,
                { message -> println("New Message: $message") },
                String::class.java
            )

            thread {
                while (disconnected){
                    try{
                        connection.start()
                        println("Connectado ${Calendar.getInstance()}")
                        disconnected = false
                    }catch (e: Exception){
                        println("Falha para conectar")
                        Thread.sleep(2000)
                    }
                }
            }
        }else{
            createConnection("AAA-1111")
        }
    }

    private fun createConnection(plate: String){
        connection = HubConnectionBuilder
            .create("${URL_BASE}$HUB?placa=$plate")
            .withHandshakeResponseTimeout(3000)
            .build()

        connection.keepAliveInterval = 1000
    }
}

/*
*
*
* namespace ConsoleTablet
{
    class Program
    {
        static HubConnection connection;
        static CancellationTokenSource tokenSource = new CancellationTokenSource();
        static CancellationToken token = tokenSource.Token;
        static string loginToken;

        static void Main(string[] args)
        {
            Login();

            connection = new HubConnectionBuilder()
               .WithUrl("https://localhost:5601/tabletHub?placa=AAA-1111")
               .WithAutomaticReconnect(new RetryPolicy())
               .Build();

            connection.Reconnecting += error =>
            {
                Debug.Assert(connection.State == HubConnectionState.Reconnecting);
                Console.WriteLine("Reconectando...");

                return Task.CompletedTask;
            };

            connection.Reconnected += connectionId =>
            {
                Debug.Assert(connection.State == HubConnectionState.Connected);
                Console.WriteLine("Reconectado!");

                return Task.CompletedTask;
            };

            connection.Closed += async (error) =>
            {
                Console.WriteLine($"Desconectado em {DateTime.Now}");
            };

            connection.On<dynamic>("RecebaTelemetria", (obj) =>
            {
                Console.WriteLine($"RecebaTelemetria: {obj}");
            });

            //Connect();
            Task.Factory.StartNew(ConnectWithRetryAsync);

            while (true)
            {
                var opcao = Console.ReadKey().KeyChar.ToString();

                if (opcao == "0")
                {
                    tokenSource.Cancel();
                    break;
                }
                else if (opcao == "1")
                {
                    EnvieComando("Iniciar");
                }
                else if (opcao == "2")
                {
                    EnvieComando("Parar");
                }
            }

            connection.StopAsync().Wait();
        }

        private static async void Connect()
        {
            try
            {
                await connection.StartAsync();
                Console.WriteLine($"Conectado em {DateTime.Now}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Falha ao conectar em {DateTime.Now}: {ex.Message}");
            }
        }

        private static async Task<bool> ConnectWithRetryAsync()
        {
            // Keep trying to until we can start or the token is canceled.
            while (true)
            {
                try
                {
                    await connection.StartAsync(token);
                    Debug.Assert(connection.State == HubConnectionState.Connected);
                    Console.WriteLine($"Conectado em {DateTime.Now}");

                    return true;
                }
                catch when (token.IsCancellationRequested)
                {
                    return false;
                }
                catch
                {
                    Console.WriteLine($"Falha ao conectar em {DateTime.Now}");
                    // Failed to connect, trying again in 2s.
                    Debug.Assert(connection.State == HubConnectionState.Disconnected);
                    await Task.Delay(2000);
                }
            }
        }

        private static async void EnvieComando(string comando)
        {
            try
            {
                await connection.SendAsync("EnvieComando", comando);
                Console.WriteLine($"Comando enviado: {comando}");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Falha ao enviar comando em {DateTime.Now}: {ex.Message}");
            }
        }

        private static bool GetVeiculo()
        {
            string placa = "aaa1111";
            var client = new RestClient("https://localhost:5601");
            var request = new RestRequest($"api/v1/veiculos/{placa}");
            request.AddHeader("x-api-key", "147864EE-28A8-4A47-8CBA-B604DEB9B2FB");
            request.AddHeader("Authorization", loginToken);

            var response = client.Get<bool>(request);
            bool retorno = false;

            if (response.StatusCode == System.Net.HttpStatusCode.OK)
            {
                retorno = response.Data;
            }

            return retorno;
        }

        private static void Login()
        {
            var client = new RestClient("https://localhost:5601");
            var request = new RestRequest("api/v1/logins");
            request.AddHeader("x-api-key", "147864EE-28A8-4A47-8CBA-B604DEB9B2FB");

            //request.JsonSerializer = new RestSharp.Serializers.Newtonsoft.Json.NewtonsoftJsonSerializer(new JsonSerializer { ContractResolver = new Newtonsoft.Json.Serialization.CamelCasePropertyNamesContractResolver() });

            LoginDTO login = new LoginDTO
            {
                Cpf = "555.555.555-55",
                IdTablet = "38f0d1b36b81856d",
                Latitude = "10",
                Longitude = "10",
                Senha = "123456"
            };

            request.AddJsonBody(login);

            IRestResponse response = client.Post(request);

            if (response.StatusCode == System.Net.HttpStatusCode.OK)
            {
                var examinador = Newtonsoft.Json.JsonConvert.DeserializeObject<ExaminadorDTO>(response.Content, new JsonSerializerSettings
                {
                    MissingMemberHandling = MissingMemberHandling.Ignore
                });
                loginToken = "Bearer " + examinador.Token;
            }
            else
            {
                throw new Exception("Falha login");
            }
        }
    }
}
*
* */