using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using Newtonsoft.Json;
using WPFDatingApp.Login;
using WPFDatingApp.Properties;

namespace WPFDatingApp.Helpers
{
    public static class HttpClientHelper
    {
        public const string ApiUrl = "http://dev.riftgen.com:8000/DatingSiteJS/";

        public static T Get<T>(string url) where T : new()
        {
            using (var handler = new HttpClientHandler() { UseCookies = false })
            {
                var httpClient = new HttpClient(handler)
                {
                    BaseAddress = new Uri(ApiUrl + url)
                };

                var message = new HttpRequestMessage(HttpMethod.Get, "");
                message.Headers.Add("Cookie", $"sessionKey={Settings.Default.SessionKey}");
                httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/xml"));

                var result = httpClient.SendAsync(message).Result;

                if (result != null)
                {
                    var doc = new XmlDocument();
                    doc.LoadXml(result.Content.ReadAsStringAsync().Result);
                    string jsonText = JsonConvert.SerializeXmlNode(doc);
                    var json = JsonConvert.DeserializeObject<T>(jsonText);

                    return json;
                }
            }

            return new T();
        }
    }
}
