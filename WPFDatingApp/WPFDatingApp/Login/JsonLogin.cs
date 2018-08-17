using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace WPFDatingApp.Login
{
    public partial class JsonLogin
    {
        [JsonProperty("?xml")]
        public Xml Xml { get; set; }

        [JsonProperty("PAGE")]
        public Page Page { get; set; }
    }

    public partial class Page
    {
        [JsonProperty("HEADER")]
        public Header Header { get; set; }

        [JsonProperty("ROWS")]
        public Rows Rows { get; set; }

        [JsonProperty("ERROR")]
        public Error Error { get; set; }
    }

    public partial class Header
    {
        [JsonProperty("QUERY_DATE_TIME")]
        public DateTimeOffset QueryDateTime { get; set; }
    }

    public partial class Rows
    {
        [JsonProperty("ROW")]
        public Row Row { get; set; }
    }

    public partial class Error
    {
        [JsonProperty("ERROR_MESSAGE")]
        public string ErrorMessage { get; set; }
    }

    public partial class Row
    {
        [JsonProperty("SESSION_KEY")]
        public string SessionKey { get; set; }

        [JsonProperty("USER_ID")]
        [JsonConverter(typeof(ParseStringConverter))]
        public long UserId { get; set; }
    }

    public partial class Xml
    {
        [JsonProperty("@version")]
        public string Version { get; set; }

        [JsonProperty("@encoding")]
        public string Encoding { get; set; }
    }

    public partial class JsonLogin
    {
        public static JsonLogin FromJson(string json) => JsonConvert.DeserializeObject<JsonLogin>(json, Converter.Settings);
    }

    public static class Serialize
    {
        public static string ToJson(this JsonLogin self) => JsonConvert.SerializeObject(self, Converter.Settings);
    }

    internal static class Converter
    {
        public static readonly JsonSerializerSettings Settings = new JsonSerializerSettings
        {
            MetadataPropertyHandling = MetadataPropertyHandling.Ignore,
            DateParseHandling = DateParseHandling.None,
            Converters = {
                new IsoDateTimeConverter { DateTimeStyles = DateTimeStyles.AssumeUniversal }
            },
        };
    }

    internal class ParseStringConverter : JsonConverter
    {
        public override bool CanConvert(Type t) => t == typeof(long) || t == typeof(long?);

        public override object ReadJson(JsonReader reader, Type t, object existingValue, JsonSerializer serializer)
        {
            if (reader.TokenType == JsonToken.Null) return null;
            var value = serializer.Deserialize<string>(reader);
            long l;
            if (Int64.TryParse(value, out l))
            {
                return l;
            }
            throw new Exception("Cannot unmarshal type long");
        }

        public override void WriteJson(JsonWriter writer, object untypedValue, JsonSerializer serializer)
        {
            if (untypedValue == null)
            {
                serializer.Serialize(writer, null);
                return;
            }
            var value = (long)untypedValue;
            serializer.Serialize(writer, value.ToString());
            return;
        }

        public static readonly ParseStringConverter Singleton = new ParseStringConverter();
    }
}
