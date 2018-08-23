using System;
using System.Collections.Generic;
using System.Globalization;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;

namespace WPFDatingApp.ApiClasses
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
        [JsonConverter(typeof(SingleOrArrayConverter<Row>))]
        public List<Row> Row { get; set; }
    }

    public partial class Error
    {
        [JsonProperty("ERROR_MESSAGE")]
        public string ErrorMessage { get; set; }
    }

    public partial class Row
    {
        [JsonProperty("LOGGED_IN")]
        public string LoggedIn { get; set; }

        [JsonProperty("SESSION_KEY")]
        public string SessionKey { get; set; }

        [JsonProperty("USER_ID")]
        [JsonConverter(typeof(ParseStringConverter))]
        public long UserId { get; set; }

        [JsonProperty("USERNAME")]
        public string Username { get; set; }

        [JsonProperty("FIRST_NAME")]
        public string FirstName { get; set; }

        [JsonProperty("LAST_NAME")]
        public string LastName { get; set; }

        [JsonProperty("EMAIL")]
        public string Email { get; set; }

        [JsonProperty("AGE")]
        public int Age { get; set; }

        [JsonProperty("DESCRIPTION")]
        public string Description { get; set; }

        [JsonProperty("PROFILE_PIC")]
        public string ProfilePicture { get; set; }

        [JsonProperty("CONTENT")]
        public string MessageContent { get; set; }

        [JsonProperty("CREATED")]
        public string MessageTimestamp { get; set; }

        [JsonProperty("FROM_USER")]
        public string MessageFromUser { get; set; }

        [JsonProperty("USER_MESSAGE_ID")]
        public string MessageUserMesssageID { get; set; }
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

    public class SingleOrArrayConverter<T> : JsonConverter
    {
        public override bool CanConvert(Type objectType)
        {
            return (objectType == typeof(List<T>));
        }

        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            JToken token = JToken.Load(reader);
            if (token.Type == JTokenType.Array)
            {
                return token.ToObject<List<T>>();
            }
            return new List<T> { token.ToObject<T>() };
        }

        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            List<T> list = (List<T>)value;
            if (list.Count == 1)
            {
                value = list[0];
            }
            serializer.Serialize(writer, value);
        }

        public override bool CanWrite
        {
            get { return true; }
        }
    }
}
